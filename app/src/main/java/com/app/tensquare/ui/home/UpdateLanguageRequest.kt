package com.app.tensquare.ui.home

data class UpdateLanguageRequest(
    val languageId: String,
    val languageName: String,
    val className: String,
    val classId: String
)