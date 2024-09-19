package com.app.tensquare.ui.home

import com.app.tensquare.ui.profile.EnrolmentPlan

data class UpdateLanguageResponse(
    val `data`: UpdateDate,
    val message: String,
    val status: String
)

data class UpdateDate(
    val languageId: String,
    val classId: String,
    val className: String,
    val language_name: String
)