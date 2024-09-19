package com.app.tensquare.ui.initialsetup

data class LanguageListResponse(
    val `data`: List<Language>,
    val message: String,
    val status: String
)

data class Language(
    val _id: String,
    val count: Int,
    val isDeleted: Int,
    val name: String,
    val status: Int
)

data class ClassListResponse(
    val classList: List<ClassData>,
    val length: Int,
    /*val message: String,*/
    val pageNo: Int/*,
    val status: String*/
)

data class ClassData(
    val _id: String,
    val count: Int,
    val createdAt: String,
    val isDeleted: Int,
    val languageData: LanguageData,
    val languageId: String,
    val name: String,
    val status: Int
){
    override fun toString(): String {
        return name
    }
}

data class LanguageData(
    val _id: String,
    val name: String
)

data class GuestTokenResponse(
    val `data`: GuestToken,
    val message: String,
    val status: String
)

data class GuestToken(
    val accessToken: String,
    val refreshToken: String
)

data class SubjectListData(
    val `data`: List<SubjectData>,
    val message: String,
    val status: String
)

data class SubjectData(
    val _id: String,
    val isDeleted: Int,
    val languageId: String,
    val name: String,
    val value: Int,
    var isSelected: Boolean = false
)