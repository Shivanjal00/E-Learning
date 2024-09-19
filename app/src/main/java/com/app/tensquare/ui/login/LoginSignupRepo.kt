package com.app.tensquare.ui.login

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.ui.forgotpassword.ForgotPasswordRequest
import com.app.tensquare.ui.otp.OtpRequest
import com.app.tensquare.ui.otp.OtpVerificationResponse
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.password.PasswordResponse
import com.google.gson.JsonObject
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class LoginSignupRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun login(loginRequest: LoginRequest): Flow<NetworkResult<LoginResponse>> {
        return flow<NetworkResult<LoginResponse>> {
            emit(safeApiCall { remoteDataSource.login(loginRequest) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyOTP(
        push_token: String,
        request: OtpRequest
    ): Flow<NetworkResult<OtpVerificationResponse>> {
        return flow<NetworkResult<OtpVerificationResponse>> {
            emit(safeApiCall { remoteDataSource.verifyOTP(push_token, request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun resendOTP(
        push_token: String,
        request: JsonObject
    ): Flow<NetworkResult<LoginResponse>> {
        return flow<NetworkResult<LoginResponse>> {
            emit(safeApiCall { remoteDataSource.resendOTP(push_token, request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun signup(
        push_token: String,
        request: SignupRequest
    ): Flow<NetworkResult<SignupResponse>> {
        return flow<NetworkResult<SignupResponse>> {
            emit(safeApiCall { remoteDataSource.signup(push_token, request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun setPassword(request: NewPasswordRequest): Flow<NetworkResult<PasswordResponse>> {
        return flow<NetworkResult<PasswordResponse>> {
            emit(safeApiCall { remoteDataSource.setPassword(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun requestForPassword(request: ForgotPasswordRequest): Flow<NetworkResult<LoginResponse>> {
        return flow<NetworkResult<LoginResponse>> {
            emit(safeApiCall { remoteDataSource.requestForPassword(request) })
        }.flowOn(Dispatchers.IO)
    }


}