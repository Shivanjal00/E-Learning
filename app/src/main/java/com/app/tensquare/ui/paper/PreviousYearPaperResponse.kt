package com.app.tensquare.ui.paper

import com.app.tensquare.base.ComingSoon

data class PreviousYearPaperResponse(
    val `data`: PreviousYearPaperData,
    val message: String,
    val status: String
)

data class PreviousYearPaperData(
    val list: List<PreviousYearPaper>,
    val length: Int,
    val pageNo: Int,
    val comingSoon: ComingSoon
)

data class PreviousYearPaper(
    val _id: String,
    val boardName: String,
    val document: String,
    val setNumber: Int,
    val title: String,
    val year: Int,
    val fileName: String,
    var isClicked: Boolean = false,
    var subjectId: String,
    val enrollmentPlanStatus: Boolean
)