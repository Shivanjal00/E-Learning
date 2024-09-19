package com.app.tensquare.ui.appdetail

data class AboutUsResponse(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val __v: Int,
    val _id: String,
    val content: String,
    val image: String
)

data class PrivacyPolicyResponse(
    val `data`: PrivacyPolicy,
    val message: String,
    val status: String
)

data class PrivacyPolicy(
    val __v: Int,
    val _id: String,
    val content: String
)

