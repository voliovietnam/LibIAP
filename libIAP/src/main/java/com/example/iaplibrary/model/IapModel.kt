package com.example.iaplibrary.model

import com.android.billingclient.api.ProductDetails

data class IapModel(
    val name: String,
    val productId: String,
    val title: String,
    val type: String,
    val subscriptionDetails: List<SubscriptionOfferDetails>?,
    val inAppDetails: InAppModel? = null,

    var isPurchase: Boolean = false,
    var purchaseTime: Long = 0L,
    var purchaseToken: String = "",
) {
    companion object {
        fun convertDataToProductPremium(listData: List<ProductDetails>): List<IapModel> {
            val listProduct = mutableListOf<IapModel>()

            listData.forEach {
                val listDetail = mutableListOf<SubscriptionOfferDetails>()
                it.subscriptionOfferDetails?.forEach {
                    val pricingPhases = mutableListOf<PricingPhases>()
                    it.pricingPhases.pricingPhaseList.forEach {
                        pricingPhases.add(
                            PricingPhases(
                                formattedPrice = it.formattedPrice,
                                priceCurrencyCode = it.priceCurrencyCode,
                                priceAmountMicros = it.priceAmountMicros,
                                billingPeriod = it.billingPeriod,
                            )
                        )
                    }


                    listDetail.add(
                        SubscriptionOfferDetails(
                            offerIdToken = it.offerToken,
                            offerId = it.offerId ?: "",
                            offerTags = if (it.offerTags.isNotEmpty()) it.offerTags.first() else "",
                            pricingPhases = pricingPhases
                        )
                    )
                }
                var inApp: InAppModel? = null
                it.oneTimePurchaseOfferDetails?.let {
                    inApp = InAppModel(
                        formattedPrice = it.formattedPrice,
                        priceAmountMicros = it.priceAmountMicros,
                        priceCurrencyCode = it.priceCurrencyCode,
                    )
                }
                val data = IapModel(
                    name = it.name,
                    productId = it.productId,
                    title = it.title,
                    type = it.productType,
                    subscriptionDetails = listDetail,
                    inAppDetails = inApp,
                )
                listProduct.add(data)
            }

            return listProduct
        }
    }
}
