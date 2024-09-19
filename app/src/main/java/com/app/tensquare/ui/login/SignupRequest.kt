package com.app.tensquare.ui.login

data class SignupRequest(
    val name: String,
    val mobile: String,
    val termCondition: Int,
    val languageId: String,
    val classId: String,
    val languageName: String,
    val className: String,
    val deviceId: String
)