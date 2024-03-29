package com.example.iaplibrary.model

data class InAppModel(
    val name: String,
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
):IapData
