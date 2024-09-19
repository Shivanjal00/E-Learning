package com.app.tensquare.ui.analysis

data class ChapterAnalysisResponse(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val averagePerQuestion: String,
    val percentOfCorrectAnswer: Int,
    val timeSpendOnPractice: String,
    val topicsForImprovement: List<TopicsForImprovement>,
    val totalTestAttemped: Int,
    val userSpendTime: String
)

data class TopicsForImprovement(
    val _id: String,
    val name: String
)