package com.app.tensquare.ui.profile


data class ProfileDetailResponse(
    val `data`: ProfileDetail,
    val message: String,
    val status: String
)

data class ProfileDetail(
    val _id: String,
    val classId: String,
    val className: String,
    val district: String,
    val email: String,
    //  val enrolmentPlan: EnrolmentPlan,
    val languageId: String,
    val languageName: String,
    val mobile: String,
    val name: String,
    val profilePic: String,
    val schoolName: String,
    val stateId: String,
    val stateName: String,
    val enrollmentPlanStatus: Boolean,
//    val enrolmentPlan: EnrolmentPlan
    val `enrolmentPlan`: List<EnrolmentPlan>

)

data class EnrolmentPlan(
    val _id: String,
    val className: String,
    val enrollmentId: String,
    val expiredDate: String,
    val subjectName: List<String>,
    val userId: String,
    val name: String,
    val offer: String
)

//data class EnrolmentPlan()


/*
data class Data(
    val _id: String,
    val classId: String,
    val className: String,
    val email: String,
    val enrolmentPlan: EnrolmentPlan,
    val languageId: String,
    val languageName: String,
    val mobile: String,
    val name: String,
    val profilePic: String,
    val stateName: String
)

class EnrolmentPlan(
)*/
