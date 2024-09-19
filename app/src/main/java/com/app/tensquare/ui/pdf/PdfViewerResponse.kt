package com.app.tensquare.ui.pdf

import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperData
import com.app.tensquare.ui.questionbankpaper.questionList

data class PdfViewerResponse (
    val `data` : QuestionBankPaperData,
    val message: String,
    val status: String,
)

data class PdfViewerData(
    val userId: String,
    val subjectId: String,
    val url: String,
    var enrollmentPlanStatus: Boolean
)