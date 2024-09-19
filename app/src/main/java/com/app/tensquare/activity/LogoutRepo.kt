package com.app.tensquare.activity

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.ui.forgotpassword.ForgotPasswordRequest
import com.app.tensquare.ui.login.LoginResponse
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.password.PasswordResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class LogoutRepo  @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun logout(): Flow<NetworkResult<LogoutResponse>> {
        return flow<NetworkResult<LogoutResponse>> {
            emit(safeApiCall { remoteDataSource.logout() })
        }.flowOn(Dispatchers.IO)
    }

}