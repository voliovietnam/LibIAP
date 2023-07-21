package com.example.iaplibrary.model

data class InAppModel(
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
)
