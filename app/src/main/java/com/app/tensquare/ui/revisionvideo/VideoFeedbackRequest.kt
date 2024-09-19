package com.app.tensquare.ui.revisionvideo

data class VideoFeedbackRequest(
    val reason: String,
    val revisionId: String
)