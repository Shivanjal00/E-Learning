package com.app.tensquare.ui.home

data class SearchRequest(
    val languageId: String,
    val pageNo: Int,
    val searchKey: String
)