package com.app.tensquare.ui.latestupdate

import com.app.tensquare.base.ComingSoon

data class LatestUpdateResponse(
    val `data`: LatestUpdate,
    val message: String,
    val status: String
)

data class LatestUpdate(
    val length: Int,
    val list: List<Update>,
    val pageNo: Int,
    val comingSoon: ComingSoon
)

data class Update(
    val _id: String,
    val document: String,
    val fileType: Int,
    val name: String,
    val thumbnail: String,
    val videoURL: String,
    var isClicked: Boolean,
    val fileName: String
)