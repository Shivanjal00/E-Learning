package com.app.tensquare.ui.login

data class SignupResponse(
    val `data`: SignUpData,
    val message: String,
    val status: String
)

data class SignUpData(
    val _id: String,
    val password: String,
    val token: String
)