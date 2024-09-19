package com.app.tensquare.ui.questionbankpaper

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
class QuestionBankPaperRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getQuestionBankPaperList(
        id: String,languageId: String, pageNo: Int
    ): Flow<NetworkResult<QuestionBankPaperData>> {
        return flow<NetworkResult<QuestionBankPaperData>> {
            emit(safeApiCall {remoteDataSource.getQuestionBankPaperList(id, languageId, pageNo) })
        }.flowOn(Dispatchers.IO)
    }


}