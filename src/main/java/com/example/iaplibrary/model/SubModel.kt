package com.example.iaplibrary.model

import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubModel(
    val name: String,
    val productId: String,
    val title: String,

    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val offerIdToken: String,
    var typeSub: TypeSub = TypeSub.Base
):IapData
