package com.app.tensquare.ui.questionbank

data class PracticeSessionTimeRequest(
    val chapterIds: String,
    val subjectId: String,
    val userSpendTime: String
)