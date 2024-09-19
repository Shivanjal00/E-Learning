package com.app.tensquare.ui.revisionvideo

data class LatestUpdateResponse(
    val `data`: NewUpdate,
    val message: String,
    val status: String
)

data class NewUpdate(
    val nextVideos: List<NextVideo>,
    val videoDetails: VideoDetails,
    val like: Int
)

data class VideoDetails(
    val _id: String,
    val document: String,
    val fileType: Int,
    val languageId: String,
    val languageName: String,
    val name: String,
    val status: Int,
    val thumbnail: String,
    val videoURL: String,
    val description: String,
    val fileName: String,
)