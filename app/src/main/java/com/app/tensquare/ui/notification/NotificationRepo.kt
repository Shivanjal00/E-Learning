package com.app.tensquare.ui.notification

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
class NotificationRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getNotificationList(request: String): Flow<NetworkResult<NotificationList>> {
        return flow<NetworkResult<NotificationList>> {
            emit(safeApiCall { remoteDataSource.getNotificationList(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteNotification(request: NotificationDeleteRequest): Flow<NetworkResult<NotificationDeleteResponse>> {
        return flow<NetworkResult<NotificationDeleteResponse>> {
            emit(safeApiCall { remoteDataSource.deleteNotification(request) })
        }.flowOn(Dispatchers.IO)
    }

}