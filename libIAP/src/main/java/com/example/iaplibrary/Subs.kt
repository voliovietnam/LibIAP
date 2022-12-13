package com.example.iaplibrary

import com.android.billingclient.api.*
import com.example.iaplibrary.model.IapIdModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Subs constructor(
    private val billingClient: BillingClient?,
    val informationProduct: (listData: List<ProductDetails>) -> Unit,
    val subscribeIap: (listData: List<Purchase>) -> Unit
) : IapInterface {

    override suspend fun getInformation(listID: List<IapIdModel>) =
        withContext(Dispatchers.Default) {
            getPriceSubscribeIap(listID)
            checkSubscribeIap()
        }

    override fun checkSubscribeIap() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases.isNotEmpty()) {
                subscribeIap(purchases)
            }
        }
    }

    override suspend fun getPriceSubscribeIap(listID: List<IapIdModel>) {
        val productList: ArrayList<QueryProductDetailsParams.Product> = ArrayList()

        listID.forEach {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder().setProductId(it.idProduct)
                    .setProductType(BillingClient.ProductType.SUBS).build()
            )
        }

        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = billingClient?.queryProductDetails(params.build())
        productDetailsResult?.productDetailsList?.let {
            informationProduct(it)
        }
    }

    override fun unSubscribeIap() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases.isNotEmpty()) {
                purchases.forEach {
                    CoroutineScope(Dispatchers.IO).launch {
                        val consumeParams =
                            ConsumeParams.newBuilder()
                                .setPurchaseToken(it.purchaseToken)
                                .build()

                        withContext(Dispatchers.IO) {
                            billingClient.consumeAsync(consumeParams) { billingResult, s ->
                            }
                        }
                    }
                }
            }
        }
    }
}
