package com.app.tensquare.ui.questionbank

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
class QuestionBankRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getTestQuestionList(testQuestionRequest: TestQuestionRequest): Flow<NetworkResult<TestQuestionList>> {
        return flow<NetworkResult<TestQuestionList>> {
            emit(safeApiCall { remoteDataSource.getTestQuestionList(testQuestionRequest) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitTestAnswers(request: TestAnswerSubmitRequest): Flow<NetworkResult<TestAnswerSubmission>> {
        return flow<NetworkResult<TestAnswerSubmission>> {
            emit(safeApiCall { remoteDataSource.submitTestAnswers(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTestAnswer(id: String): Flow<NetworkResult<TestAnswerSheet>> {
        return flow<NetworkResult<TestAnswerSheet>> {
            emit(safeApiCall { remoteDataSource.getTestAnswer(id) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPracticeSessionQuestionList(answerSubmissionRequest: PracticeQuestionRequest): Flow<NetworkResult<PracticeQuestionListData>> {
        return flow<NetworkResult<PracticeQuestionListData>> {
            emit(safeApiCall {
                remoteDataSource.getPracticeSessionQuestionList(
                    answerSubmissionRequest
                )
            })
        }.flowOn(Dispatchers.IO)
    }

   suspend fun submitPracticeSessionTime(request: PracticeSessionTimeRequest): Flow<NetworkResult<String>> {
        return flow<NetworkResult<String>> {
            emit(safeApiCall {
                remoteDataSource.submitPracticeSessionTime(
                    request
                )
            })
        }.flowOn(Dispatchers.IO)
    }


}