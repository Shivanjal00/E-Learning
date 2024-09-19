package com.app.tensquare.ui.revisionvideo

data class LikeVideoRequest(
    val userId: String,
    val videoId: String,
    val status: Int
)
