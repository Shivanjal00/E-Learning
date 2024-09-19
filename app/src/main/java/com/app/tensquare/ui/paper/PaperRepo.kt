package com.app.tensquare.ui.paper

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
class PaperRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getModelPaperList(
        id: String,
        pageNo: Int,
        languageId: String
    ): Flow<NetworkResult<ModelPaperData>> {
        return flow<NetworkResult<ModelPaperData>> {
            emit(safeApiCall { remoteDataSource.getModelPaperList(id, pageNo , languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getHomeModelPaperList(
        pageNo: Int,  languageId: String
    ): Flow<NetworkResult<ModelPaperData>> {
        return flow<NetworkResult<ModelPaperData>> {
            emit(safeApiCall { remoteDataSource.getHomeModelPaperList(pageNo , languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPreviousYearPaperList(
        id: String, pageNo: Int , languageId: String
    ): Flow<NetworkResult<PreviousYearPaperData>> {
        return flow<NetworkResult<PreviousYearPaperData>> {
            emit(safeApiCall { remoteDataSource.getPreviousYearPaperList(id, pageNo ,languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getHomePreviousYearPaperList(pageNo: Int , languageId: String): Flow<NetworkResult<PreviousYearPaperData>> {
        return flow<NetworkResult<PreviousYearPaperData>> {
            emit(safeApiCall { remoteDataSource.getHomePreviousYearPaperList(pageNo, languageId) })
        }.flowOn(Dispatchers.IO)
    }


    /*suspend fun downloadPdf(fileUrl: String): Flow<NetworkResult<ResponseBody>> {
        return flow<NetworkResult<ResponseBody>> {
            emit(safeApiCall { remoteDataSource.downloadPdf(fileUrl) })
        }.flowOn(Dispatchers.IO)
    }*/


}