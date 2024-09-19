package com.app.tensquare.ui.revisionvideo

data class SearchedVideoDetailResponse(
    val `data`: SearchedVideoDetail,
    val message: String,
    val status: String
)

data class SearchedVideoDetail(
    val _id: String,
    val description: String,
    val thumbnail: String,
    val title: String,
    val videoURL: String
)