package com.app.tensquare.ui.questionbank

data class QuesCount(
    var id: String = "",
    val no: Int,
    var isSelected: Boolean,
    var isBookMarked: Boolean = false
)
