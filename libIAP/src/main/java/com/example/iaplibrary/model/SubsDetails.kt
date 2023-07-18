package com.example.iaplibrary.model

data class SubsDetails(


    var priceAmountMicrosBase: Long = 0L,
    var formattedPriceBase: String ="",
    var billingPeriod: String = "P1W",
    var recurrenceMode: Int = 1,
    var tokenBase : String = "",

    var hasFreeTrialOffer: Boolean = false,
    var tokenTrialOffer : String =  "",

    var hasSaleOffer: Boolean = false,
    var tokenSaleOffer : String = "",
    var priceAmountMicrosSale: Long = 0L,
    var formattedPriceSale: String = ""

)