package com.app.tensquare.base

import android.content.Intent
import com.app.tensquare.HiltApplication
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.ui.session.PracticeSessionListResponse
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.google.gson.JsonDeserializer
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error(response.code(), false, response.message())
        } catch (exception: Exception) {

            when (exception) {
                is HttpException -> {
                    return error(exception.code(), false, exception.message())
                }
                is IOException -> {
                    return error(
                        null,
                        true,
                        exception.message
                    )
                }
                else ->
                    return error(
                        null,
                        true,
                        exception.message
                    )
            }

            //return error(e.message ?: e.toString())
        }
    }

     fun <T> error(
        errorCode: Int?,
        isNetworkError: Boolean?,
        errorBody: String?
    ): NetworkResult<T> =
        NetworkResult.Error(errorBody.toString())
       // NetworkResult.Error("Api call failed ${errorBody.toString()}")
}