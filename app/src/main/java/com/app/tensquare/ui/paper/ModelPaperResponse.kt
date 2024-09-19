package com.app.tensquare.ui.paper

import com.app.tensquare.base.ComingSoon

data class ModelPaperResponse(
    val `data`: ModelPaperData,
    val message: String,
    val status: String
)

data class ModelPaperData(
    val list: List<ModelPaper>,
    val length: Int,
    val pageNo: Int,
    val comingSoon: ComingSoon
)

data class ModelPaper(
    val _id: String,
    val document: String,
    val subTitle: String,
    val title: String,
    val fileName: String,
    var isClicked: Boolean = false,
    var subjectId: String,
    val enrollmentPlanStatus: Boolean
)