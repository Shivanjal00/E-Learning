package com.app.tensquare.ui.session

import android.content.Intent
import android.util.Log
import com.app.tensquare.HiltApplication
import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.ui.login.LoginActivity
import com.app.tensquare.utils.ACCESS_TOKEN_EXPIRED
import com.app.tensquare.utils.LOG_OUT
import com.app.tensquare.utils.OTHER_DEVISE_LOGIN
import com.app.tensquare.utils.REFRESH_TOKEN_EXPIRED
import com.app.tensquare.utils.STATUS_SUCCESS
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class SessionVideoRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getPracticeSessionList(
        id: String
    ): Flow<NetworkResult<PracticeSessionListResponse>> {
        return flow<NetworkResult<PracticeSessionListResponse>> {
            emit(safeApiCall { remoteDataSource.getPracticeSessionList(id) })
        }.flowOn(Dispatchers.IO)
    }

    /*suspend fun getRevisionVideoDetail(
        videoId: String,
    ): Flow<NetworkResult<RevisionVideoDetailResponse>> {
        return flow<NetworkResult<RevisionVideoDetailResponse>> {
            emit(safeApiCall { remoteDataSource.getRevisionVideoDetail(videoId) })
        }.flowOn(Dispatchers.IO)
    }*/

    /*suspend fun getFAQS(): Flow<NetworkResult<FaqsResponse>> {
        return flow<NetworkResult<FaqsResponse>> {
            emit(safeApiCall { remoteDataSource.getFAQS() })
        }.flowOn(Dispatchers.IO)
    }*/


}