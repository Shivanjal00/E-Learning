package com.app.tensquare.ui.notes

import com.app.tensquare.base.ComingSoon

data class NotesListResponse(
    val `data`: NotesListResponseData,
    val message: String,
    val status: String
)

data class NotesListResponseData(
    val length: Int,
    val list: List<Notes>,
    val pageNo: Int,
    val comingSoon: ComingSoon
)

data class Notes(
    val _id: String,
    val description: String,
    val document: List<String>,
    val fileName: List<String>,
    val title: String,
    var isClicked: Boolean = false,
    var subjectId: String,
    val lock: Int
)

/*data class Notes(
    val _id: String,
    val chapterId: String,
    val chapterName: String,
    val classId: String,
    val className: String,
    val createdAt: String,
    val description: String,
    val document: List<String>,
    val isDeleted: Int,
    val languageId: String,
    val languageName: String,
    val status: Int,
    val subjectId: String,
    val subjectName: String,
    val title: String,
    var isClicked: Boolean = false
)*/
/*
data class NotesListResponse(
    val `data`: List<Notes>,
    val length: Int,
    val message: String,
    val pageNo: Int,
    val status: String
)

data class Notes(
    val _id: String,
    val chapterId: String,
    val chapterName: String,
    val classId: String,
    val className: String,
    val createdAt: String,
    val description: String,
    val document: List<String>,
    val isDeleted: Int,
    val languageId: String,
    val languageName: String,
    val status: Int,
    val subjectId: String,
    val subjectName: String,
    val title: String,
    var isClicked: Boolean = false
)*/
