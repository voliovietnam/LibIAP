package com.example.iaplibrary

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.example.iaplibrary.model.IapData
import com.example.iaplibrary.model.IapIdModel
import com.example.iaplibrary.model.IapModel
import com.example.iaplibrary.model.IapModel.Companion.convertDataToProductPremium
import com.example.iaplibrary.model.InAppModel
import com.example.iaplibrary.model.SubModel
import com.example.iaplibrary.model.TypeSub
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArrayList

object IapConnector {
    private var pathJson: String? = null

    private var billingClient: BillingClient? = null

    private val listProductModel = CopyOnWriteArrayList<IapModel>()
    private var productModelOlder: IapModel? = null
    private val productDetailsList = CopyOnWriteArrayList<ProductDetails>()
    private val listID = CopyOnWriteArrayList<IapIdModel>()

    private var jobCountTimeConnectIap: Job? = null

    private var inApp: InApp? = null
    private var subs: Subs? = null
    private var isDebug: Boolean? = null

    val listPurchased = MutableLiveData<List<IapModel>?>(null)

    private val subscribeInterface = CopyOnWriteArrayList<SubscribeInterface>()

    fun initIap(application: Application, pathJson: String, isDebug: Boolean? = null) {

        this.pathJson = pathJson
        this.isDebug = isDebug
        billingClient =
            BillingClient.newBuilder(application).setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val promise = async {
                            for (purchase in purchases) {
                                //   val job = CoroutineScope(Dispatchers.IO).async {
                                handlePurchase(purchase, true)
                                //   }
                            }
                        }
                        promise.await()
                        val data = mutableListOf<IapModel>()
                        listProductModel.iterator().forEach {
                            if (it.isPurchase) {
                                data.add(it)
                            }
                        }
                        listPurchased.postValue(data)
                        //  val data= listProductModel.iterator().forEach {  }
                    }
                } else {
                    subscribeInterface.iterator().forEach { subscribe ->
                        subscribe.subscribeError(logEventFailed(billingResult.responseCode))
                    }
                }
            }.enablePendingPurchases().build()

        listID.addAll(IapIdModel.getDataInput(application, pathJson))

        jobCountTimeConnectIap = CoroutineScope(Dispatchers.IO).launch {
            delay(3_000)
            listPurchased.postValue(emptyList())
        }

        inApp = InApp(billingClient, informationProduct = {
            productDetailsList.addAll(it)
            listProductModel.addAll(convertDataToProductPremium(it))
        }, subscribeIap = {

            CoroutineScope(Dispatchers.IO).launch {
                val promise = async {
                    for (purchase in it) {
                        //  val job = CoroutineScope(Dispatchers.IO).async {
                        handlePurchase(purchase, false)
                        //   }
                    }
                }
                promise.await()
                val data = mutableListOf<IapModel>()
                listProductModel.iterator().forEach {
                    if (it.isPurchase) {
                        data.add(it)
                    }
                }
                listPurchased.postValue(data)
            }

        })

        subs = Subs(billingClient, informationProduct = {
            productDetailsList.addAll(it)
            listProductModel.addAll(convertDataToProductPremium(it))
        }, subscribeIap = {

            CoroutineScope(Dispatchers.IO).launch {
                val promise = async {
                    for (purchase in it) {
                        // val job = CoroutineScope(Dispatchers.IO).async {
                        handlePurchase(purchase, true)
                        // }
                    }
                }
                promise.await()
                val data = mutableListOf<IapModel>()
                listProductModel.iterator().forEach {
                    if (it.isPurchase) {
                        data.add(it)
                    }
                }
                listPurchased.postValue(data)
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

    fun buyIap(
        activity: Activity, productId: String, idToken: String
    ) {
        productDetailsList.iterator().forEach { productDetails ->
            if (productDetails.productId == productId) {
                val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)

                if (productDetails.productType == BillingClient.ProductType.SUBS) {
                    billingFlowParam.setOfferToken(idToken)
                }

                val productDetailsParamsList = listOf(billingFlowParam.build())

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList).build()

                billingClient?.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

    fun buyIapUpgrade(
        activity: Activity, productId: String, productIdOlder: String, idToken: String
    ) {
        productDetailsList.iterator().forEach { productDetails ->
            if (productDetails.productId == productId) {
                val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)

                if (productDetails.productType == BillingClient.ProductType.SUBS) {
                    billingFlowParam.setOfferToken(
                        idToken
                    )
                }

                val productDetailsParamsList = listOf(billingFlowParam.build())

                val billingFlowParams = BillingFlowParams.newBuilder()
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
        jobCountTimeConnectIap?.cancel()
        // isPurchasesIap.postValue(true)

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
                Log.d("HeinDxxx", "setDataCallBackSuccess: $it")

                it.isPurchase = true
                it.purchaseTime = purchase.purchaseTime
                it.purchaseToken = purchase.purchaseToken
                if (isSubscriptions) {
                    subscribeInterface.iterator().forEach { subscribe ->
                        subscribe.subscribeSuccess(it)

                        val oldListPurchased =
                            listPurchased.value?.toMutableList() ?: mutableListOf()
                        oldListPurchased.add(it)
                        listPurchased.postValue(oldListPurchased)
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

    fun getAllProductModel(): List<IapModel> {
        return listProductModel
    }

    fun inAppInformation(productId: String): InAppModel? {
        listProductModel.find { it.productId == productId && it.type == INAPP }?.let {
            it.inAppDetails?.let {
                return it
            }
        }
        return null
    }

    fun subSaleInformation(productId: String): SubModel? {
        listProductModel.find { it.productId == productId && it.type == SUBS }?.let {
            it.subscriptionDetails?.find { it.typeSub == TypeSub.Sale }?.let {
                return it
            }
        }
        return null
    }

    fun subTrailInformation(productId: String): SubModel? {
        listProductModel.find { it.productId == productId && it.type == SUBS }?.let {
            it.subscriptionDetails?.find { it.typeSub == TypeSub.Trail }?.let {
                return it
            }
        }
        return null
    }

    fun subBaseInformation(productId: String): SubModel? {
        listProductModel.find { it.productId == productId && it.type == SUBS }?.let {
            it.subscriptionDetails?.find { it.typeSub == TypeSub.Base }?.let {
                return it
            }
        }
        return null
    }

    fun subInformation(productId: String, typeSub: TypeSub): SubModel? {
        listProductModel.find { it.productId == productId && it.type == SUBS }?.let {
            it.subscriptionDetails?.find { it.typeSub == typeSub }?.let {
                return it
            }
        }
        return null
    }

    fun listSubInformation(productId: String): List<SubModel> {
        val listSub = mutableListOf<SubModel>()
        subTrailInformation(productId)?.let {
            listSub.add(it)
        }
        subSaleInformation(productId)?.let {
            listSub.add(it)
        }
        subBaseInformation(productId)?.let {
            listSub.add(it)
        }
        return listSub
    }

    fun iapInformation(productId: String): List<IapData> {
        val listIap = mutableListOf<IapData>()
        when (typeIap(productId)) {
            INAPP -> {
                inAppInformation(productId)?.let {
                    listIap.add(it)
                }
            }

            SUBS -> {
                listIap.addAll(listSubInformation(productId))
            }

            else -> {

            }
        }
        return listIap
    }

    fun typeIap(productId: String): String? {
        listProductModel.find { it.productId == productId }?.let {
            return it.type
        }
        return null
    }

    fun percentSale(productId: String): Int {
        return if (typeIap(productId) == SUBS) {
            try {
                val base = subBaseInformation(productId)?.let {
                    it.priceAmountMicros
                } ?: 0
                val sale = subSaleInformation(productId)?.let {
                    it.priceAmountMicros
                } ?: 0

                (sale * 100 / base).toInt()

            } catch (e: Exception) {
                0
            }

        } else {
            0
        }
    }

    fun buyIap(
        activity: Activity,
        productId: String,
        typeSub: TypeSub = TypeSub.Base,
    ) {
        val token = when (typeIap(productId)) {
            INAPP -> {
                "INAPP"
            }

            SUBS -> {
                val tokenBase = subBaseInformation(productId)?.offerIdToken ?: ""
                when (typeSub) {
                    TypeSub.Trail -> {
                        subTrailInformation(productId)?.offerIdToken ?: tokenBase
                    }

                    TypeSub.Sale -> {
                        subSaleInformation(productId)?.offerIdToken ?: tokenBase
                    }

                    else -> {
                        subBaseInformation(productId)?.offerIdToken
                    }
                }
            }

            else -> {
                null
            }
        }

        token?.let {
            buyIap(activity, productId, it)
        }
    }

    const val INAPP = "inapp"
    const val SUBS = "subs"
}
