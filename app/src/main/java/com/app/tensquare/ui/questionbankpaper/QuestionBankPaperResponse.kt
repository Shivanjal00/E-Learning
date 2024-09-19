package com.app.tensquare.ui.questionbankpaper

data class QuestionBankPaperResponse(
    val `data` : QuestionBankPaperData,
    val message: String,
    val status: String,
)

data class QuestionBankPaperData(
    val questionList: List<questionList>,
    val length: Int,
    val pageNo: Int
)

data class questionList(
    val _id: String,
    val name: String,
    val document: String,
    val languageId: String,
    val languageName: String,
    val classId: String,
    val className: String,
    val subjectId: String,
    val subjectName: String,
    val thumbnail: String,
    val description: String,
    val fileName: String,
    val status: Int,
    val createdAt: String
)