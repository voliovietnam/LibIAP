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
    val offerToken: String? = null,
    val offerTokenSale: String? = null,
) {
    companion object {
        fun convertDataToProduct(listData: List<ProductDetails>): List<ProductModel> {
            val listProduct = mutableListOf<ProductModel>()
            listData.forEach {
//                val list = it.subscriptionOfferDetails?.get(
//                    0
//                )?.pricingPhases?.pricingPhaseList
//
//                it.subscriptionOfferDetails
                var formattedPriceSecond = ""
                var formattedPrice :String = ""
                var offerToken: String? = null
                var offerTokenSale: String? = null
                if (it.productType == "subs"){
                    if (it.subscriptionOfferDetails != null) {
                        if (it.subscriptionOfferDetails?.size == 1) {
                            offerToken = it?.subscriptionOfferDetails!![0].offerToken
                            formattedPrice = it?.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
                        } else {
                            offerToken = it?.subscriptionOfferDetails!![0].offerToken
                            offerTokenSale = it?.subscriptionOfferDetails!![1].offerToken
                            formattedPriceSecond = it?.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
                            formattedPrice = it?.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[1].formattedPrice
                        }
                    }
                }else{
                    formattedPrice =  it.oneTimePurchaseOfferDetails?.formattedPrice!!
                }






                Log.d("dsk1", "convertDataToProduct: $it")
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
                    formattedPrice = formattedPrice,
                    isPurchase = false,
                    formattedPriceSecond = formattedPriceSecond,
                    offerToken =  offerToken,
                    offerTokenSale = offerTokenSale
                )



//                val data = ProductModel(
//                    productId = it.productId,
//                    type = it.productType,
//                    title = it.title,
//                    description = it.description,
//                    priceCurrencyCode = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
//                        0
//                    )?.pricingPhases?.pricingPhaseList?.get(
//                        0
//                    )?.priceCurrencyCode
//                        ?: "" else it.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: "",
//                    formattedPrice = if (it.productType == "subs") it.subscriptionOfferDetails?.get(
//                        0
//                    )?.pricingPhases?.pricingPhaseList?.get(
//                        0
//                    )?.formattedPrice ?: "" else it.oneTimePurchaseOfferDetails?.formattedPrice
//                        ?: "",
//                    isPurchase = false,
//                    formattedPriceSecond = ""
//                )



                listProduct.add(data)
            }
            return listProduct
        }
    }
}
