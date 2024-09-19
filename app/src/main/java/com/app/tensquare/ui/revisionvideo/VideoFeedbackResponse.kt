package com.app.tensquare.ui.revisionvideo

data class VideoFeedbackResponse(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val reason: String,
    val revisionId: String,
    val userId: String
)