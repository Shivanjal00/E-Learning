package com.app.tensquare.ui.subscription

data class CouponApplicationRequest(
    val `data`: CouponData,
    val message: String,
    val status: String
)

data class CouponData(
    val _id: String,
    val amount: Int,
    val isPercent: Boolean
)