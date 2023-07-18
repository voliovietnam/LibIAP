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
    var subsDetails: SubsDetails
) {
    companion object {
        fun convertDataToProduct(listData: List<ProductDetails>): List<ProductModel> {
            val listProduct = mutableListOf<ProductModel>()
            listData.forEach {
                val list = it.subscriptionOfferDetails?.get(
                    0
                )?.pricingPhases?.pricingPhaseList


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
                    },
                    subsDetails = getSubsDetails(it)
                )
                listProduct.add(data)
            }
            return listProduct
        }

        fun getSubsDetails(productDetails: ProductDetails): SubsDetails {

            val subsDetails = SubsDetails()

            var base: ProductDetails.SubscriptionOfferDetails? = null
            var offerTrial: ProductDetails.SubscriptionOfferDetails? = null
            var offerSale: ProductDetails.SubscriptionOfferDetails? = null


            base = productDetails.subscriptionOfferDetails?.lastOrNull()
            Log.e(
                "DucLH---offerBase",
                base?.pricingPhases?.pricingPhaseList?.firstOrNull()?.priceAmountMicros?.toString()
                    ?: ""
            )

            //get TrialOffer
            productDetails.subscriptionOfferDetails?.forEach { offer ->
                offer?.pricingPhases?.pricingPhaseList?.forEach {
                    val isTrial = it.priceAmountMicros == 0L && it.recurrenceMode == 2
                    if (isTrial) {
                        offerTrial = offer
                    }
                }
            }

            // getSaleOffer
            productDetails.subscriptionOfferDetails?.forEach { offer ->
                val pricingPhaseList = offer?.pricingPhases?.pricingPhaseList ?: return@forEach

                if ((pricingPhaseList.size) >= 2) {
                    val firstPrice = pricingPhaseList.first().priceAmountMicros
                    val lastPrice = pricingPhaseList.last().priceAmountMicros

                    Log.e("DucLH---offer", "firstPrice" + firstPrice)
                    Log.e("DucLH---offer", "lastPrice" + lastPrice)

                    if (firstPrice != 0L && firstPrice < lastPrice) {
                        offerSale = offer
                    }
                }
            }

            Log.e("DucLH---offer", "offerSale" + offerSale?.pricingPhases)
            Log.e("DucLH---offer", "offerTrial" + offerTrial?.pricingPhases)

            base?.let {
                subsDetails.apply {
                    priceAmountMicrosBase =
                        it.pricingPhases.pricingPhaseList.get(0)?.priceAmountMicros ?: 0L
                    formattedPriceBase =
                        it.pricingPhases.pricingPhaseList.get(0)?.formattedPrice ?: ""
                    billingPeriod = it.pricingPhases.pricingPhaseList.get(0)?.billingPeriod ?: "P1W"
                    recurrenceMode = it.pricingPhases.pricingPhaseList.get(0)?.recurrenceMode ?: 1
                    tokenBase = it.offerToken
                }
            }

            offerTrial?.let {
                subsDetails.apply {
                    hasFreeTrialOffer = true
                    tokenTrialOffer = it.offerToken
                }
            }

            offerSale?.let {
                subsDetails.apply {
                    hasSaleOffer = true
                    priceAmountMicrosSale =
                        it.pricingPhases.pricingPhaseList.get(0).priceAmountMicros
                    formattedPriceSale = it.pricingPhases.pricingPhaseList.get(0).formattedPrice
                    tokenSaleOffer = it.offerToken
                }
            }
            return subsDetails
        }
    }
}

