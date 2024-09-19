package com.app.tensquare.ui.transaction

data class CreateOrderResponse(
    val `data`: CreateOrderData,
    val message: String,
    val status: String
)

data class CreateOrderData(
    val access_key: String,
    val amount: Int,
    val razorpayOrderId: String,
    val secret_key: String,
    val promoCode: String,
    val promoAmount: Int,
    val finalAmount: String
)