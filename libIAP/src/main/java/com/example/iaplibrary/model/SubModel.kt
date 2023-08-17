package com.example.iaplibrary.model

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
