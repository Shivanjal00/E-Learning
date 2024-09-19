package com.app.tensquare.ui.home

import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.response.RefreshTokenData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class HomeDataRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getHomeData(
        token: String,
        request: HomeDataRequest
    ): Flow<NetworkResult<HomeData>> {
        return flow<NetworkResult<HomeData>> {
            emit(safeApiCall { remoteDataSource.getHomeData(token, request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRefreshToken(token: String): Flow<NetworkResult<RefreshTokenData>> {
        return flow<NetworkResult<RefreshTokenData>> {
            emit(safeApiCall { remoteDataSource.getRefreshToken(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchSomething(searchRequest: SearchRequest): Flow<NetworkResult<SearchData>> {
        return flow<NetworkResult<SearchData>> {
            emit(safeApiCall { remoteDataSource.searchSomething(searchRequest) })
        }.flowOn(Dispatchers.IO)
    }

}