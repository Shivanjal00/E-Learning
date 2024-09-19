package com.app.tensquare.ui.latestupdate

import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class LatestUpdateRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getLatestUpdateList(
        pageNo: Int , languageId : String
    ): Flow<NetworkResult<LatestUpdate>> {
        return flow<NetworkResult<LatestUpdate>> {
            emit(safeApiCall { remoteDataSource.getLatestUpdateList( pageNo , languageId)})
        }.flowOn(Dispatchers.IO)
    }


}