package com.app.tensquare.ui.transaction

data class SignVerificationRequest(
    val enrollmentId: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String,
    val secret: String,
    val promoCode: String,
    val promoAmount: Int,
    val finalAmount: String
)