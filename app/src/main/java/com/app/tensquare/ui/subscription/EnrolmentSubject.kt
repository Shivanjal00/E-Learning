package com.app.tensquare.ui.subscription

/*data class EnrolmentSubject(
    val _id: String,
    val className: String,
    val crossAmount: Int,
    val finalAmount: Int,
    val subjectName: String,
    var isSelected: Boolean = false
)*/

data class EnrolmentSubject(
    val _id: String,
    val className: String,
    val crossAmount: Int,
    val finalAmount: Int,
    val expiredDate: String,
    val subjectName: List<String>,
    var isSelected: Boolean = false,

    var currentPlan: Boolean = false,
    val name: String,
    val offer: String

)
