package com.app.tensquare.ui.questionbank

import com.app.tensquare.base.ComingSoon

data class PracticeQuestionListResponse(
    val `data`: PracticeQuestionListData,
    val message: String,
    val status: String,
    val comingSoon: ComingSoon?
)

data class PracticeQuestionListData(
    val questionList: List<PracticeQuestion>,
    val pageNo: Int,
    val length: Int,
    val answerSubmission: Int,
    val comingSoon: ComingSoon?
)

data class PracticeQuestion(
    val _id: String,
    val name: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correct: String,
    val answerDetails: String,
    val difficultyLevel: String
)