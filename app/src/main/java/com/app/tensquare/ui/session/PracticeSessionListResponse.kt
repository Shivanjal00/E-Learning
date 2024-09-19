package com.app.tensquare.ui.session

import com.app.tensquare.base.ComingSoon

data class PracticeSessionListResponse(
    val comingSoon: ComingSoon?,
    val data: List<PracticeSession> = ArrayList(),
    val message: String,
    val status: String
)

data class PracticeSession(
    val _id: String,
    val description: String,
    val name: String,
    var isChecked: Boolean = true,
    val lock:Int
)