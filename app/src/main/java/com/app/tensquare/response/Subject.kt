package com.app.tensquare.response

import kotlin.random.Random

data class Subject(
    val id: Int = Random(1).nextInt(),
    val name: String = "",
    val image: Int = 0,
    val bg: Int = 0
)

data class PracticeSession(
    val id: Int = Random(1).nextInt(),
    var name: String = "Coordinate Geometry",
    val image: Int = 0,
    val bg: Int = 0,
    var isChecked: Boolean = false
)

data class SelfAnalysisSubject(
    val id: Int,
    val name: String = "",
    val image: Int = 0,
    var isSelected: Boolean = false
)

data class SubjectToEnrol(
    val id: Int = Random(1).nextInt(),
    val name: String,
    val image: Int = 0,
    val bg: Int = 0,
    var isSelected: Boolean = false
)

data class RefreshTokenData(
    var accessToken: String,
    var refreshToken: String
)
