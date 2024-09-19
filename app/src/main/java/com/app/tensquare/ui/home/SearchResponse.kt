package com.app.tensquare.ui.home

data class SearchResponse(
    val `data`: SearchData,
    val message: String,
    val status: String
)

data class SearchData(
    val docs: List<Doc>,
    val length: Int,
    val pageNo: Int
)

data class Doc(
    val _id: String,
    val description: String,
    val document: List<String>?,
    val thumbnail: String,
    val title: String,
    val videoURL: String?,
    var isClicked: Boolean,
    var enrollmentPlanStatus: Boolean
)