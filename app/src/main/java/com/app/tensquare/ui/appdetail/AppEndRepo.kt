package com.app.tensquare.ui.appdetail

import com.app.tensquare.base.BaseApiResponse
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.ui.revisionvideo.VideoFeedbackRequest
import com.app.tensquare.ui.revisionvideo.VideoFeedbackResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class AppEndRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse() {

    suspend fun getAboutUs(languageId: String , type: String): Flow<NetworkResult<AboutUsResponse>> {
        return flow<NetworkResult<AboutUsResponse>> {
            emit(safeApiCall { remoteDataSource.getAboutUs(languageId , type) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPrivacyPolicy(languageId: String , type: String): Flow<NetworkResult<PrivacyPolicyResponse>> {
        return flow<NetworkResult<PrivacyPolicyResponse>> {
            emit(safeApiCall { remoteDataSource.getPrivacyPolicy(languageId , type) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getFAQS(languageId: String): Flow<NetworkResult<FaqsResponse>> {
        return flow<NetworkResult<FaqsResponse>> {
            emit(safeApiCall { remoteDataSource.getFAQS(languageId) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun contactUs(message: String): Flow<NetworkResult<ContactUsResponse>> {
        return flow<NetworkResult<ContactUsResponse>> {
            emit(safeApiCall { remoteDataSource.contactUs(message) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postFeedbackOnVideo(request: VideoFeedbackRequest): Flow<NetworkResult<VideoFeedbackResponse>> {
        return flow<NetworkResult<VideoFeedbackResponse>> {
            emit(safeApiCall { remoteDataSource.postFeedbackOnVideo(request) })
        }.flowOn(Dispatchers.IO)
    }

    /*suspend fun resendOTP(request: JsonObject): Flow<NetworkResult<LoginResponse>> {
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