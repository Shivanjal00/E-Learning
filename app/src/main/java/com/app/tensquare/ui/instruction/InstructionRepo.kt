package com.app.tensquare.ui.instruction

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
class InstructionRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getInstructionList(request: HashMap<String, String>): Flow<NetworkResult<InstructionData>> {
        return flow<NetworkResult<InstructionData>> {
            emit(safeApiCall { remoteDataSource.getInstructionList(request) })
        }.flowOn(Dispatchers.IO)
    }


}