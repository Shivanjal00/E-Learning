package com.app.tensquare.ui.notes

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class NotesRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getNotesList(request: Map<String, String>): Flow<NetworkResult<NotesListResponse>> {
        return flow<NetworkResult<NotesListResponse>> {
            emit(safeApiCall { remoteDataSource.getNotesList(request)})

            /*try {
                val response = remoteDataSource.getNotesList(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        emit(NetworkResult.Success(body))
                    }
                }
//                emit(error(response.code(), false, response.message()))
            } catch (exception: Exception) {

                when (exception) {
                    is HttpException -> {
                        emit(error(exception.code(), false, exception.message()))
                    }
                    is IOException -> {
                        emit(error(
                            null,
                            true,
                            exception.message
                        ))
                    }
                    else ->
                        emit(error(
                            null,
                            true,
                            exception.message
                        ))
                }

                //return error(e.message ?: e.toString())
            }*/
        }.flowOn(Dispatchers.IO)
    }

    /*suspend fun getPreviousYearPaperList(
        id: String, pageNo: Int
    ): Flow<NetworkResult<PreviousYearPaperResponse>> {
        return flow<NetworkResult<PreviousYearPaperResponse>> {
            emit(safeApiCall { remoteDataSource.getPreviousYearPaperList(id, pageNo) })
        }.flowOn(Dispatchers.IO)
    }*/

    /*suspend fun getFAQS(): Flow<NetworkResult<FaqsResponse>> {
        return flow<NetworkResult<FaqsResponse>> {
            emit(safeApiCall { remoteDataSource.getFAQS() })
        }.flowOn(Dispatchers.IO)
    }*/


}