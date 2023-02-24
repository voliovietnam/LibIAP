package com.example.iaplibrary.model

import android.util.Log
import com.android.billingclient.api.ProductDetails

data class ProductModel(
    val productId: String,
    val type: String,
    val title: String,
    val description: String,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    var purchaseTime: Long = 0L,
    var isPurchase: Boolean,
    var purchaseToken: String = "",
    val formattedPriceSecond: String = "",
    ) {
    companion object {
        fun convertDataToProduct(listData: List<ProductDetails>): List<ProductModel> {
            val listProduct = mutableListOf<ProductModel>()
            listData.forEach {
                val list = it.subscriptionOfferDetails?.get(
                    0
                )?.pricingPhases?.pricingPhaseList

//                val formattedPriceSecond:String = list?.let {
//                    if (it.size > 1) {
//                        it[1].formattedPrice
//                    } else {
//                        ""
//                    }
//                } ?: kotlin.run {
//                    ""
//                }

//                val price = it.subscriptionOfferDetails?.get(
//                    0
//                )?.pricingPhases?.pricingPhaseList?.get(
//                    0
//                )?.formattedPrice


//                Log.d("", "convertDataToProduct: ${listData}")
//
//                val price = it.subscriptionOfferDetails?.get(
//                    0
//                )?.pricingPhases?.pricingPhaseList
//
//                Log.d("dsk3", "id:${it.productId} -price: ${price?.size}")

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
                        ?: "" else it.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: "",
                    formattedPrice = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.formattedPrice ?: "" else it.oneTimePurchaseOfferDetails?.formattedPrice
                        ?: "",
                    isPurchase = false,
                    formattedPriceSecond = list?.let {
                        if (it.size > 1) {
                            it[1].formattedPrice
                        } else {
                            ""
                        }
                    } ?: kotlin.run {
                        ""
                    }


                )
                listProduct.add(data)
            }
            return listProduct
        }
    }
}
