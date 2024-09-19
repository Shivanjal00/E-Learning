package com.app.tensquare.ui.password

data class NewPasswordRequest(
    val password: String,
    val confirmPassword: String
)