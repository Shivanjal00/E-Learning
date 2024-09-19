package com.app.tensquare.ui.questionbank

import com.app.tensquare.ui.analysis.TopicsForImprovement

data class TestAnswerSubmissionResponse(
    val `data`: TestAnswerSubmission,
    val message: String,
    val status: String
)

data class TestAnswerSubmission(
    val averagePerQuestion: String,
    val percentOfCorrectAnswer: Double,
    val questionIds: List<String>,
    val userCorrectAnswer: Int,
    val userInCorrectAnswer: Int,
    val userSpendTime: String,
    val userUnAnswered: Int,
    val topicsForImprovement: List<TopicsForImprovement>
)