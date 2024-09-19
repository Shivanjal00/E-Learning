package com.app.tensquare.ui.profile

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

@ActivityRetainedScoped
class ProfileRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getProfileDetail(token: String): Flow<NetworkResult<ProfileDetailResponse>> {
        return flow<NetworkResult<ProfileDetailResponse>> {
            emit(safeApiCall { remoteDataSource.getProfileDetail(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStateList(languageId: String): Flow<NetworkResult<StateListResponse>> {
        return flow<NetworkResult<StateListResponse>> {
            emit(safeApiCall { remoteDataSource.getStateList(languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateProfileDetails(body: MultipartBody): Flow<NetworkResult<UpdateProfileResponse>> {
        return flow<NetworkResult<UpdateProfileResponse>> {
            emit(safeApiCall { remoteDataSource.updateProfileDetails(body) })
        }.flowOn(Dispatchers.IO)
    }


}