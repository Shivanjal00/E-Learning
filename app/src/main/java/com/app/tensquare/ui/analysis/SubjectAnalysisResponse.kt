package com.app.tensquare.ui.analysis

data class SubjectAnalysisResponse(
    val `data`: SubjectAnalysis,
    val message: String,
    val status: String
)

data class SubjectAnalysis(
    val averagePerQuestion: String,
    val percentOfCorrectAnswer: Int,
    val timeSpendOnPractice: String,
    /*val topicsForImprovement: List<Any>,*/
    val totalTestAttemped: String,
    val userSpendTime: String
)