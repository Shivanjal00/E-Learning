package com.app.tensquare.ui.appdetail

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.revisionvideo.VideoFeedbackRequest
import com.app.tensquare.ui.revisionvideo.VideoFeedbackResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppEndViewModel @Inject constructor(private val repository: AppEndRepo) :
    ViewModel() {

    private val _aboutUsResponse: MutableLiveData<NetworkResult<AboutUsResponse>> =
        MutableLiveData()
    val aboutUsResponse: LiveData<NetworkResult<AboutUsResponse>> = _aboutUsResponse

    fun getAboutUs(languageId: String , type: String) = viewModelScope.launch {
        repository.getAboutUs(languageId , type).collect { values ->
            _aboutUsResponse.value = values
        }
    }

   private val _privacyPolicyResponse: MutableLiveData<NetworkResult<PrivacyPolicyResponse>> =
        MutableLiveData()
    val privacyPolicyResponse: LiveData<NetworkResult<PrivacyPolicyResponse>> = _privacyPolicyResponse

    fun getPrivacyPolicy(languageId: String , type: String) = viewModelScope.launch {
        repository.getPrivacyPolicy(languageId , type).collect { values ->
            _privacyPolicyResponse.value = values
        }
    }

    private val _faqsResponse: MutableLiveData<NetworkResult<FaqsResponse>> =
        MutableLiveData()
    val faqsResponse: LiveData<NetworkResult<FaqsResponse>> = _faqsResponse

    fun getFAQS(languageId: String) = viewModelScope.launch {
        repository.getFAQS(languageId).collect { values ->
            _faqsResponse.value = values
        }
    }

    private val _contactUsResponse: MutableLiveData<NetworkResult<ContactUsResponse>> =
        MutableLiveData()
    val contactUsResponse: LiveData<NetworkResult<ContactUsResponse>> = _contactUsResponse

    fun contactUs(message: String) = viewModelScope.launch {
        repository.contactUs(message).collect { values ->
            _contactUsResponse.value = values
        }
    }

    private val _videoFeedbackResponse: MutableLiveData<NetworkResult<VideoFeedbackResponse>> =
        MutableLiveData()
    val videoFeedbackResponse: LiveData<NetworkResult<VideoFeedbackResponse>> =
        _videoFeedbackResponse

    fun postFeedbackOnVideo(request: VideoFeedbackRequest) = viewModelScope.launch {
        repository.postFeedbackOnVideo(request).collect { values ->
            _videoFeedbackResponse.value = values
        }
    }

    /*private val _resendOtpResponse: MutableLiveData<NetworkResult<LoginResponse>> =
         MutableLiveData()
     val resendOtpResponse: LiveData<NetworkResult<LoginResponse>> = _resendOtpResponse

     fun resendOTP(request: JsonObject) = viewModelScope.launch {
         repository.resendOTP(request).collect { values ->
             _resendOtpResponse.value = values
         }
     }

     private val _signupResponse: MutableLiveData<NetworkResult<SignupResponse>> = MutableLiveData()
     val signupResponse: LiveData<NetworkResult<SignupResponse>> = _signupResponse

     fun signup(request: SignupRequest) = viewModelScope.launch {
         repository.signup(request).collect { values ->
             _signupResponse.value = values
         }
     }

     private val _passwordResponse: MutableLiveData<NetworkResult<PasswordResponse>> =
         MutableLiveData()
     val passwordResponse: LiveData<NetworkResult<PasswordResponse>> = _passwordResponse

     fun setPassword(request: NewPasswordRequest) = viewModelScope.launch {
         repository.setPassword(request).collect { values ->
             _passwordResponse.value = values
         }
     }

     private val _forgotPasswordResponse: MutableLiveData<NetworkResult<LoginResponse>> =
         MutableLiveData()
     val forgotPasswordResponse: LiveData<NetworkResult<LoginResponse>> = _forgotPasswordResponse

     fun requestForPassword(request: ForgotPasswordRequest) = viewModelScope.launch {
         repository.requestForPassword(request).collect { values ->
             _forgotPasswordResponse.value = values
         }
     }*/
}