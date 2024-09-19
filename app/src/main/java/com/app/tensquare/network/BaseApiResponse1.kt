package com.app.tensquare.network

import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.app.tensquare.HiltApplication
import com.app.tensquare.base.ComingSoon
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseApiResponse1 {
    suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseData<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                println("response###$response")
                if (response.status == STATUS_SUCCESS)
                    NetworkResult.Success(response.data, response.message)
                else {
                    if (response.message == ACCESS_TOKEN_EXPIRED)
                         error(200, false, response.message, response.data)
                    //error(ACCESS_TOKEN_EXPIRED)
                    else if (response.message == REFRESH_TOKEN_EXPIRED) {
                        Log.e("Logout =>" , "1")
                    /*    val intent =  Intent(HiltApplication.context, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        HiltApplication.context?.sendBroadcast(broadcast)
                        HiltApplication.context?.startActivity(intent)
                        HiltApplication.context?.finish()   */

                        error(200, false, response.message, response.data)
                        //error(REFRESH_TOKEN_EXPIRED)

                    }else if (response.message == OTHER_DEVISE_LOGIN){
                        Log.e("Logout =>" , "2")
                       /* val intent =  Intent(HiltApplication.context, LoginActivity::class.java)
                        intent.putExtra("Expired", "Expired")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val broadcast = Intent(LOG_OUT)
                        HiltApplication.context?.sendBroadcast(broadcast)
                        HiltApplication.context?.startActivity(intent)
                        HiltApplication.context?.finish()*/

                        error(200, false, response.message, response.data)

                    } else {
                        Log.e("Logout =>" , "3 = ${response.data}")
                         error(200, false, response.message, response.data)
                        //error("Api call failed ${response.status}")
                    }
                }
                //return error(200, false, response.message)
                //return error(response.message)
            } catch (exception: Exception) {
                when (exception) {
                    is HttpException -> {
                         error(exception.code(), false, exception.message())
                    }
                    is IOException -> {
                         error(
                            null,
                            true,
                            exception.message
                        )
                    }
                    else ->
                         error(
                            null,
                            true,
                            exception.message
                        )
                }
                /*println("response###${e.message}")
                println("response###${e.stackTrace}")
                return error(e.message ?: e.toString())*/
            }
        }
    }

     fun <T> error(
        errorCode: Int?,
        isNetworkError: Boolean?,
        errorBody: String?,
        comingSoon: T?= null
    ): NetworkResult<T> =
        NetworkResult.Error("${errorBody.toString()}", comingSoon)

    /*private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)*/
}


