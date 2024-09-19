package com.app.tensquare.network

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val comingSoon: T? = null
) {
    class Success<T>(data: T, message: String? = null) : NetworkResult<T>(data, message)

    class Error<T>(message: String, data: T? = null,comingSoon: T? = null) : NetworkResult<T>(data, message, comingSoon)

    class Loading<T> : NetworkResult<T>()

}