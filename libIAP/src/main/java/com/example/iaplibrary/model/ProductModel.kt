package com.example.iaplibrary.model

import com.android.billingclient.api.ProductDetails

data class ProductModel(
    val productId: String,
    val type: String,
    val title: String,
    val description: String,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    var purchaseTime: Long = 0L,
    var isPurchase: Boolean
) {
    companion object {
        fun convertDataToProduct(listData: List<ProductDetails>): List<ProductModel> {
            val listProduct = mutableListOf<ProductModel>()
            listData.forEach {

                val data = ProductModel(
                    productId = it.productId,
                    type = it.productType,
                    title = it.title,
                    description = it.description,
                    priceCurrencyCode = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.priceCurrencyCode
                        ?: "" else it.oneTimePurchaseOfferDetails?.priceCurrencyCode
                        ?: "",
                    formattedPrice = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.formattedPrice ?: "" else it.oneTimePurchaseOfferDetails?.formattedPrice
                        ?: "",
                    isPurchase = false,
                )
                listProduct.add(data)
            }
            return listProduct
        }
    }
}
