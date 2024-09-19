package com.app.tensquare.ui.analysis

data class ChapterAnalysisListResponse(
    val `data`: List<ChapterData>,
    val message: String,
    val status: String
)

data class ChapterData(
    val _id: String,
    val description: String,
    val name: String
)