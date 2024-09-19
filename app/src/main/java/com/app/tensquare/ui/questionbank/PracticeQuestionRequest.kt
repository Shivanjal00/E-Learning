package com.app.tensquare.ui.questionbank

data class PracticeQuestionRequest(
    val subjectId: String,
    val chapterIds: String,
    val answerBaseQuestion: ArrayList<Answer>,
    var pageNo: Int
)

data class Answer(
    val _id: String,
    val difficultyLevel: String,
    val answer: Int
)