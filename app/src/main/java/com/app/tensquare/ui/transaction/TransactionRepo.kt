package com.app.tensquare.ui.transaction

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
class TransactionRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getTxnList(): Flow<NetworkResult<TxnListResponse>> {
        return flow<NetworkResult<TxnListResponse>> {
            emit(safeApiCall { remoteDataSource.getTxnList() })
        }.flowOn(Dispatchers.IO)
    }


}