package com.app.tensquare.ui.revisionvideo

import com.app.tensquare.base.ComingSoon

data class RevisionVideoListResponse(
    val `data`: VideoListData,
    val message: String,
    val status: String
)

data class VideoListData(
    val list: ArrayList<RevisionVideo>,
    val length: Int,
    val pageNo: Int,
    val comingSoon: ComingSoon?
)

data class RevisionVideo(
    val _id: String,
    val thumbnail: String,
    val title: String,
    val description: String,
    val lock:Int
)