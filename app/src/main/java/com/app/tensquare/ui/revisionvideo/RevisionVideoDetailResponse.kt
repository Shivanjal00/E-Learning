package com.app.tensquare.ui.revisionvideo

data class RevisionVideoDetailResponse(
    val `data`: VideoDetailData,
    val message: String,
    val status: String
)

data class VideoDetailData(
    val videoDetails: VideoDetail,
    val like: Int,
    val nextVideos: List<NextVideo>
)

data class VideoDetail(
    val _id: String,
    val chapterId: String,
    val description: String,
    val subjectId: String,
    val thumbnail: String,
    val title: String,
    val videoURL: String,
    var enrollmentPlanStatus: Boolean,
    val lock: Int
)

data class NextVideo(
    val _id: String,
    val thumbnail: String,
    val title: String,
    val orderNumber: Int,
    val lock: Int
)