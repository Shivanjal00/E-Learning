package com.app.tensquare.ui.otp

import com.app.tensquare.ui.profile.EnrolmentPlan

data class OtpVerificationResponse(
    val `data`: OTPData,
    val message: String,
    val status: String
)

data class OTPData(
    val id: String,
    val mobile: String,
    val name: String,
    val token: String,
    val languageId: String,
    val classId: String,
    val refreshToken: String,
    val className: String,
    val languageName: String,
    val profilePic: String,
    val enrollmentPlanStatus: Boolean,
//    val getEnrollmentPlan: EnrollmentPlan
    val `enrolmentPlan`: List<EnrolmentPlan>
)

data class EnrollmentPlan(
    val _id: String,
    val className: String,
    val enrollmentId: String,
    val expiredDate: String,
    val subjectName: List<String>,
    val userId: String
)