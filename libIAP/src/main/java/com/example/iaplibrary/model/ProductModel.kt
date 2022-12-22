package com.example.iaplibrary.model

import com.android.billingclient.api.ProductDetails

data class ProductModel(
    val productId: String,
    val type: String,
    val title: String,
    val name: String,
    val description: String,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    var purchaseTime: Long = 0L,
    var isPurchase: Boolean,
    var purchaseToken: String = "",
    var expiry: Expiry = Expiry.PFV,
) {
    companion object {
        fun convertDataToProduct(
            listData: List<ProductDetails>, listID: List<IapIdModel>
        ): List<ProductModel> {
            val listProduct = mutableListOf<ProductModel>()
            listData.forEach {

                val data = ProductModel(
                    productId = it.productId,
                    type = it.productType,
                    title = it.title,
                    name = it.name,
                    description = it.description,
                    priceCurrencyCode = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.priceCurrencyCode
                        ?: "" else it.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: "",
                    formattedPrice = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.formattedPrice ?: "" else it.oneTimePurchaseOfferDetails?.formattedPrice
                        ?: "",
                    isPurchase = false,
                    expiry = listID.find { iap ->
                        it.productId == iap.idProduct
                    }?.expiry?.convertExpiry() ?: Expiry.PFV
                )
                listProduct.add(data)
            }
            return listProduct
        }
    }

}
