package com.example.iaplibrary.model

import kotlinx.android.parcel.Parcelize

@Parcelize
data class InAppModel(
    val name: String,
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String
):IapData
