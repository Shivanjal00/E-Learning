package com.app.tensquare.ui.analysis

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
class AnalysisRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getSubjectWiseAnalysis(subjectId: String): Flow<NetworkResult<SubjectAnalysisResponse>> {
        return flow<NetworkResult<SubjectAnalysisResponse>> {
            emit(safeApiCall { remoteDataSource.getSubjectWiseAnalysis(subjectId) })
        }.flowOn(Dispatchers.IO)
    }

   suspend fun getSubjectWiseChapterAnalysisList(subjectId: String): Flow<NetworkResult<ChapterAnalysisListResponse>> {
        return flow<NetworkResult<ChapterAnalysisListResponse>> {
            emit(safeApiCall { remoteDataSource.getSubjectWiseChapterAnalysisList(subjectId) })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getChapterWiseAnalysis(id: String): Flow<NetworkResult<ChapterAnalysisResponse>> {
        return flow<NetworkResult<ChapterAnalysisResponse>> {
            emit(safeApiCall { remoteDataSource.getChapterWiseAnalysis(id) })
        }.flowOn(Dispatchers.IO)
    }

   /* suspend fun createOrder(request: String): Flow<NetworkResult<CreateOrderResponse>> {
        return flow<NetworkResult<CreateOrderResponse>> {
            emit(safeApiCall { remoteDataSource.createOrder(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifySignature(request: SignVerificationRequest): Flow<NetworkResult<CreateOrderResponse>> {
        return flow<NetworkResult<CreateOrderResponse>> {
            emit(safeApiCall { remoteDataSource.verifySignature(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun applyCoupon(request: String): Flow<NetworkResult<CouponApplicationRequest>> {
        return flow<NetworkResult<CouponApplicationRequest>> {
            emit(safeApiCall { remoteDataSource.applyCoupon(request) })
        }.flowOn(Dispatchers.IO)
    }*/

}