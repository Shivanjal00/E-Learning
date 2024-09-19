package com.app.tensquare.ui.subscription

data class SubscriptionDataResponse(
    val `data`: SubscriptionDetail,
    val message: String,
    val status: String
)

data class SubscriptionDetail(
    val _id: String,
    val crossAmount: Int,
    val finalAmount: Int
)