package com.app.tensquare.ui.profile

data class StateListResponse(
    val `data`: List<State>,
    val message: String,
    val status: String
)

data class State(
    val _id: String,
    val name: String
){
    override fun toString(): String {
        return name
    }
}