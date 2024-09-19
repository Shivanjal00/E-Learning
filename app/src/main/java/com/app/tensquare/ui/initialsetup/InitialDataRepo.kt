package com.app.tensquare.ui.initialsetup

import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.response.RefreshTokenData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class InitialDataRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    suspend fun getGuestToken(deviceId: String): Flow<NetworkResult<GuestToken>> {
        return flow<NetworkResult<GuestToken>> {
            emit(safeApiCall { remoteDataSource.getGuestToken(deviceId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getLanguageList(): Flow<NetworkResult<List<Language>>> {
        return flow<NetworkResult<List<Language>>> {
            emit(safeApiCall { remoteDataSource.getLanguageList() })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getClassList(languageId: String): Flow<NetworkResult<ClassListResponse>> {
        return flow<NetworkResult<ClassListResponse>> {
            emit(safeApiCall { remoteDataSource.getClassList(languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSubjectList(languageId: String): Flow<NetworkResult<List<SubjectData>>> {
        return flow<NetworkResult<List<SubjectData>>> {
            emit(safeApiCall { remoteDataSource.getSubjectList(languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRefreshToken(token: String): Flow<NetworkResult<RefreshTokenData>> {
        return flow<NetworkResult<RefreshTokenData>> {
            emit(safeApiCall { remoteDataSource.getRefreshToken(token) })
        }.flowOn(Dispatchers.IO)
    }

    /*suspend fun verifyOTP(request: OtpRequest): Flow<NetworkResult<OtpVerificationResponse>> {
        return flow<NetworkResult<OtpVerificationResponse>> {
            emit(safeApiCall { remoteDataSource.verifyOTP(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun resendOTP(request: JsonObject): Flow<NetworkResult<LoginResponse>> {
        return flow<NetworkResult<LoginResponse>> {
            emit(safeApiCall { remoteDataSource.resendOTP(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun signup(request: SignupRequest): Flow<NetworkResult<SignupResponse>> {
        return flow<NetworkResult<SignupResponse>> {
            emit(safeApiCall { remoteDataSource.signup(request) })
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
    }*/


}