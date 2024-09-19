package com.app.tensquare.ui.appdetail

data class FaqsResponse(
    val `data`: List<FAQ>,
    val message: String,
    val status: String
)

data class FAQ(
    val _id: String,
    val answer: String,
    val question: String
)

data class ContactUsResponse(
    val message: String,
    val status: String
)