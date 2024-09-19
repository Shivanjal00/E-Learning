package com.app.tensquare.ui.questionbank

data class TestAnswerSubmitRequest(
    val answerDetails: List<AnswerDetail>,
    val totalTimeOfQuestions: String,
    val userSpendTime: String,
    val subjectId: String,
    val chapterIds: List<String>
)

data class AnswerDetail(
    val id: String,
    var answer: String,
    var position: Int
)