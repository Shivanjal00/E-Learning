package com.app.tensquare.ui.home

data class HomeDataResponse(
    val `data`: HomeData,
    val message: String,
    val status: String
)

data class HomeData(
//    val latestUpdates: List<LatestUpdate>,
//    val previousYearPapers: List<PreviousYearPaper>,
    val subjects: List<Subject>,
    val notificationCount: Int
)

data class Subject(
    val _id: String,
    val languageId: String,
    val name: String,
    val value: Int,
    val enrollmentPlanStatus: Boolean,
    val revisionVideoStatus : Int = 1,
    val questionBankStatus : Int = 1
)

data class LatestUpdate(
    val _id: String,
    val document: String,
    val fileType: Int,
    val name: String,
    val videoURL: String
)

data class PreviousYearPaper(
    val _id: String,
    val boardName: String,
    val document: String,
    val languageId: String,
    val setNumber: Int,
    val title: String,
    val year: Int
)

