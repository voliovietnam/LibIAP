package com.example.iaplibrary.model

data class PricingPhases(
    val formattedPrice: String,
    val priceCurrencyCode: String,
    val priceAmountMicros: Long,
    val billingPeriod: String,
)
