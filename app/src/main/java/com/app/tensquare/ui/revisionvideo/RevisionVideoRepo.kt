package com.app.tensquare.ui.revisionvideo

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
class RevisionVideoRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getRevisionVideoList(request: Map<String, String>): Flow<NetworkResult<VideoListData>> {
        return flow<NetworkResult<VideoListData>> {
            emit(safeApiCall { remoteDataSource.getRevisionVideoList(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRevisionVideoDetail(
        videoId: String,
    ): Flow<NetworkResult<VideoDetailData>> {
        return flow<NetworkResult<VideoDetailData>> {
            emit(safeApiCall { remoteDataSource.getRevisionVideoDetail(videoId) })
        }.flowOn(Dispatchers.IO)
    }

  suspend fun geSearchedtRevisionVideoDetail(
        videoId: String,
    ): Flow<NetworkResult<SearchedVideoDetail>> {
        return flow<NetworkResult<SearchedVideoDetail>> {
            emit(safeApiCall { remoteDataSource.geSearchedtRevisionVideoDetail(videoId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getLatestUpdateDetail(
        id: String,
    ): Flow<NetworkResult<NewUpdate>> {
        return flow<NetworkResult<NewUpdate>> {
            emit(safeApiCall { remoteDataSource.getLatestUpdateDetail(id) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun doLikeVideo(
        request: LikeVideoRequest,
    ): Flow<NetworkResult<Data1>> {
        return flow<NetworkResult<Data1>> {
            emit(safeApiCall { remoteDataSource.doLikeVideo(request) })
        }.flowOn(Dispatchers.IO)
    }

    /*suspend fun getFAQS(): Flow<NetworkResult<FaqsResponse>> {
        return flow<NetworkResult<FaqsResponse>> {
            emit(safeApiCall { remoteDataSource.getFAQS() })
        }.flowOn(Dispatchers.IO)
    }*/


}