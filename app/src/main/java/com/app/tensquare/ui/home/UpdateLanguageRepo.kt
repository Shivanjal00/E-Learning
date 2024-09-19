package com.app.tensquare.ui.home

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class UpdateLanguageRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun updateLanguage(request: UpdateLanguageRequest): Flow<NetworkResult<UpdateLanguageResponse>> {
        return flow<NetworkResult<UpdateLanguageResponse>> {
            emit(safeApiCall { remoteDataSource.updateLanguage(request) })
        }.flowOn(Dispatchers.IO)
    }

}