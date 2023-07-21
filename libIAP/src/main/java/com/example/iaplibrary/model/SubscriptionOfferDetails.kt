package com.example.iaplibrary.model

data class SubscriptionOfferDetails(
    val offerIdToken: String,
    val offerId: String,
    val offerTags: String,
    val pricingPhases: List<PricingPhases>
)
