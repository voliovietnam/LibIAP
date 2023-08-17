package com.example.iaplibrary.model

import com.android.billingclient.api.ProductDetails

data class IapModel(
    val type: String,
    val productId: String,
    val subscriptionDetails: List<SubModel>?,
    val inAppDetails: InAppModel? = null,

    var isPurchase: Boolean = false,
    var purchaseTime: Long = 0L,
    var purchaseToken: String = "",
) {
    companion object {

        fun convertDataToProductPremium(listData: List<ProductDetails>): List<IapModel> {
            val listProduct = mutableListOf<IapModel>()

            listData.forEach { product ->
                val listDetail = mutableListOf<SubModel>()
                product.subscriptionOfferDetails?.forEach {
                    var pricingPhases: PricingPhases? = null
                    var typeSub: TypeSub = TypeSub.Base

                    it.pricingPhases.pricingPhaseList.forEach {
                        val check = PricingPhases(
                            formattedPrice = it.formattedPrice,
                            priceCurrencyCode = it.priceCurrencyCode,
                            priceAmountMicros = it.priceAmountMicros,
                            billingPeriod = it.billingPeriod,
                        )

                        pricingPhases?.let {
                            if (it.priceAmountMicros != 0L) {
                                if (it.priceAmountMicros > check.priceAmountMicros) {
                                    pricingPhases = check
                                }
                                typeSub = TypeSub.Sale
                            }
                        } ?: kotlin.run {
                            pricingPhases = check
                            typeSub = when (check.priceAmountMicros) {
                                0L -> {
                                    TypeSub.Trail
                                }

                                else -> TypeSub.Base
                            }
                        }
                    }

                    listDetail.add(
                        SubModel(
                            name = product.name,
                            productId = product.productId,
                            title = product.title,
                            formattedPrice = pricingPhases!!.formattedPrice,
                            priceAmountMicros = pricingPhases!!.priceAmountMicros,
                            priceCurrencyCode = pricingPhases!!.priceCurrencyCode,
                            offerIdToken = it.offerToken,
                            typeSub = typeSub,
                        )
                    )
                }
                var inApp: InAppModel? = null
                product.oneTimePurchaseOfferDetails?.let {
                    inApp = InAppModel(
                        name = product.name,
                        productId = product.productId,
                        title = product.title,
                        formattedPrice = it.formattedPrice,
                        priceAmountMicros = it.priceAmountMicros,
                        priceCurrencyCode = it.priceCurrencyCode,
                    )
                }
                val data = IapModel(
                    productId = product.productId,
                    type = product.productType,
                    subscriptionDetails = listDetail,
                    inAppDetails = inApp,
                )
                listProduct.add(data)
            }

            return listProduct
        }
    }
}
