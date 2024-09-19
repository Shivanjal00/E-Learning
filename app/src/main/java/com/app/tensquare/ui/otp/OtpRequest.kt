package com.app.tensquare.ui.otp

data class OtpRequest(
    val token: String,
    val otp: String,
    val languageId: String,
    val languageName: String,
    val classId: String,
    val className: String,
    val deviceId: String,
    val isDeleted:String = ""
    /*,
   val push_token: String*/
)