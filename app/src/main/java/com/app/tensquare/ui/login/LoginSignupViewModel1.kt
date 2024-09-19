package com.app.tensquare.ui.login

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel1 @Inject constructor(private val repository: LoginSignupRepo) :
    ViewModel() {

    /*private val _response: MutableLiveData<NetworkResult<LoginResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<LoginResponse>> = _response

    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        repository.login(loginRequest).collect { values ->
            _response.value = values
        }
    }

    private val _verifyOtpResponse: MutableLiveData<NetworkResult<OtpVerificationResponse>> =
        MutableLiveData()
    val verifyOtpResponse: LiveData<NetworkResult<OtpVerificationResponse>> = _verifyOtpResponse

    fun verifyOTP(request: OtpRequest) = viewModelScope.launch {
        repository.verifyOTP(request).collect { values ->
            _verifyOtpResponse.value = values
        }
    }

    private val _resendOtpResponse: MutableLiveData<NetworkResult<LoginResponse>> =
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