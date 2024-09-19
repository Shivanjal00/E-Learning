package com.app.tensquare.ui.pdf

import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class PdfViewerRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getVerifyUserEnrollmentPlan(
        subjectId: String, url: String
    ): Flow<NetworkResult<PdfViewerData>> {
        return flow<NetworkResult<PdfViewerData>> {
            emit(safeApiCall {remoteDataSource.getVerifyUserEnrollmentPlan(subjectId, url) })
        }.flowOn(Dispatchers.IO)
    }
}