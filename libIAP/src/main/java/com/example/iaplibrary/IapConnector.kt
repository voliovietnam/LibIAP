package com.example.iaplibrary

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.example.iaplibrary.model.IapIdModel
import com.example.iaplibrary.model.ProductModel
import com.example.iaplibrary.model.ProductModel.Companion.convertDataToProduct
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

object IapConnector {
    private var pathJson: String? = null

    private var billingClient: BillingClient? = null

    private val listProductModel = CopyOnWriteArrayList<ProductModel>()
    private var productModelOlder: ProductModel? = null
    private val productDetailsList = CopyOnWriteArrayList<ProductDetails>()
    private val listID = CopyOnWriteArrayList<IapIdModel>()

    private var jobCountTimeConnectIap: Job? = null

    private var inApp: InApp? = null
    private var subs: Subs? = null
    private var isDebug: Boolean? = null

    val isPurchasesIap = MutableLiveData<Boolean?>(null)
//    val subscribeSuccess = MutableLiveData<ProductModel?>(null)
//    val subscribeError = MutableLiveData<String?>(null)

    private val subscribeInterface = CopyOnWriteArrayList<SubscribeInterface>()

    fun initIap(application: Application, pathJson: String, isDebug: Boolean? = null) {


        this.pathJson = pathJson
        this.isDebug = isDebug
        billingClient =
            BillingClient.newBuilder(application).setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        CoroutineScope(Dispatchers.IO).launch {
                            handlePurchase(purchase, true)
                        }
                    }
                } else {
                    subscribeInterface.iterator().forEach { subscribe ->
                        subscribe.subscribeError(logEventFailed(billingResult.responseCode))
                    }
                }
            }.enablePendingPurchases().build()

        listID.addAll(IapIdModel.getDataInput(application, pathJson))

        jobCountTimeConnectIap = CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            isPurchasesIap.postValue(false)
        }

        inApp = InApp(billingClient, informationProduct = {
            productDetailsList.addAll(it)
            listProductModel.addAll(convertDataToProduct(it))
        }, subscribeIap = {
            for (purchase in it) {
                CoroutineScope(Dispatchers.IO).launch {
                    handlePurchase(purchase, false)
                }
            }
        })

        subs = Subs(billingClient, informationProduct = {
            Log.d("dsk", "informationProduct: $it")
            it.forEach {
                Log.e("DucLH---ProductDetails", it.toString())
                Log.e("DucLH---PrDetailsToken", it.subscriptionOfferDetails?.size.toString())

            }
            productDetailsList.addAll(it)
            listProductModel.addAll(convertDataToProduct(it))
        }, subscribeIap = {
            for (purchase in it) {
                CoroutineScope(Dispatchers.IO).launch {
                    handlePurchase(purchase, false)
                }
            }
        })

        startConnection()
    }

    fun addIAPListener(listener: SubscribeInterface) {
        subscribeInterface.add(listener)
    }

    fun removeIAPListener(listener: SubscribeInterface) {
        subscribeInterface.remove(listener)
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val inappsubs = mutableListOf<IapIdModel>()
                        listID.iterator().forEach {
                            if (it.type == "inapp") inappsubs.add(it)
                        }

                        if (inappsubs.isNotEmpty()) {
                            inApp?.getInformation(inappsubs)
                        }
                        val subssubs = mutableListOf<IapIdModel>()

                        listID.iterator().forEach {
                            if (it.type == "subs") subssubs.add(it)
                        }
                        if (subssubs.isNotEmpty()) {
                            subs?.getInformation(listID.filter { it.type == "subs" })
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
            }
        })
    }

    fun buyIap(activity: Activity, productId: String, isPrioritizeTrial: Boolean = true) {
        productDetailsList.iterator().forEach { productDetails ->
            if (productDetails.productId == productId) {
                val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)

                if (productDetails.productType == BillingClient.ProductType.SUBS) {

                    val productModel = listProductModel.find {
                        it.productId == productId
                    }

                    var token = productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
                    productModel?.let { model ->
                        if (isPrioritizeTrial) {
                            if (model.subsDetails.hasFreeTrialOffer && model.subsDetails.tokenTrialOffer.isNotBlank()) {
                                token = model.subsDetails.tokenTrialOffer
                            } else if (model.subsDetails.tokenSaleOffer.isNotBlank()) {
                                token = model.subsDetails.tokenSaleOffer
                            }
                        } else if (model.subsDetails.tokenSaleOffer.isNotBlank()) {
                            token = model.subsDetails.tokenSaleOffer
                        }
                    }

                    billingFlowParam.setOfferToken(token)
                }

                val productDetailsParamsList =
                    listOf(billingFlowParam.build())

                val billingFlowParams =
                    BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                billingClient?.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

    fun buyIapUpgrade(activity: Activity, productId: String, productIdOlder: String) {
        productDetailsList.iterator().forEach { productDetails ->
            if (productDetails.productId == productId) {
                val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)

                if (productDetails.productType == BillingClient.ProductType.SUBS) {
                    billingFlowParam.setOfferToken(
                        productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
                    )
                }

                val productDetailsParamsList =
                    listOf(billingFlowParam.build())

                val billingFlowParams =
                    BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)

                var purchaseToken: String? = null

                listProductModel.iterator().forEach {
                    if (it.isPurchase && it.productId == productIdOlder) {
                        productModelOlder = it
                        purchaseToken = it.purchaseToken
                    }
                }

                purchaseToken?.let {
                    billingFlowParams.setSubscriptionUpdateParams(
                        BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                            .setOldPurchaseToken(it)
                            .setReplaceProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_WITHOUT_PRORATION)
                            .build()
                    )
                }

                billingClient?.launchBillingFlow(activity, billingFlowParams.build())
            }
        }
    }

    private fun handlePurchase(purchase: Purchase, isSubscriptions: Boolean) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
            CoroutineScope(Dispatchers.Default).launch() {
                withContext(Dispatchers.IO) {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) { p0 ->
                        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                            setDataCallBackSuccess(purchase, isSubscriptions)
                        } else {
                            subscribeInterface.iterator().forEach { subscribe ->
                                subscribe.subscribeError(logEventFailed(p0.responseCode))
                            }
                        }
                    }
                }
            }
        } else {
            setDataCallBackSuccess(purchase, isSubscriptions)
        }
    }

    fun resetIap(activity: Activity) {
//        if (BuildConfig.DEBUG) {
//            Toast.makeText(activity, "Reset IAP", Toast.LENGTH_SHORT).show()
//            subs?.unSubscribeIap()
//            inApp?.unSubscribeIap()
//        }
        isDebug?.let {
            if (it) {
                Toast.makeText(activity, "Reset IAP", Toast.LENGTH_SHORT).show()
                subs?.unSubscribeIap()
                inApp?.unSubscribeIap()
            }
        }
    }

    private fun setDataCallBackSuccess(purchase: Purchase, isSubscriptions: Boolean) {
        Log.d("HIHIHIHIHI", "setDataCallBackSuccess: ${purchase.originalJson}")
        jobCountTimeConnectIap?.cancel()
        isPurchasesIap.postValue(true)

        productModelOlder?.let { pro ->
            listProductModel.iterator().forEach {
                if (it.productId == pro.productId) {
                    it.isPurchase = false
                    it.purchaseToken = ""
                }
            }
            productModelOlder = null
        }

        listProductModel.iterator().forEach {
            if (purchase.products.contains(it.productId)) {
                it.isPurchase = true
                it.purchaseTime = purchase.purchaseTime
                it.purchaseToken = purchase.purchaseToken
                if (isSubscriptions) {
                    subscribeInterface.iterator().forEach { subscribe ->
                        subscribe.subscribeSuccess(it)
                    }
                }
            }
        }
    }

    private fun logEventFailed(code: Int): String {
        return when (code) {
            -3 -> "Service_Timeout"
            -2 -> "Feature_Not_Supported"
            -1 -> "Service_Disconnected"
            1 -> "User_Canceled"
            2 -> "Service_Unavailable"
            3 -> "Billing_Unavailable"
            4 -> "Item_Unavailable"
            5 -> "Developer_Error"
            6 -> "Error"
            7 -> "Item_Already_Owned"
            8 -> "Item_Not_Owned"
            else -> ""
        }
    }

    fun getAllProductModel(): List<ProductModel> {
        return listProductModel
    }
}
