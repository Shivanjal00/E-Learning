package com.app.tensquare.ui.paper

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val _responseBody: MutableLiveData<ResponseBody> =
        MutableLiveData()
    val responseBody: LiveData<ResponseBody> =
        _responseBody

    fun downloadPdf(url: String) = viewModelScope.launch {
        /*val downloadService: ApiService =
            ApiClient.client.create(ApiService::class.java)*/

        val call: Call<ResponseBody> = apiService.downloadPdf(url,sharedPrefManager.getUserToken() ?: "")

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "server contacted and has file")
                    _responseBody.value = response.body()
                    // val writtenToDisk: Boolean = writeResponseBodyToDisk(response.body())
                    //Log.d("TAG", "file download was a success? $writtenToDisk")
                } else {
                    Log.d("TAG", "server contact failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("TAG", "error")
            }
        })
    }


}