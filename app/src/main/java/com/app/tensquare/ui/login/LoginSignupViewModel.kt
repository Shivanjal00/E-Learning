package com.app.tensquare.ui.login

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.forgotpassword.ForgotPasswordRequest
import com.app.tensquare.ui.otp.OtpRequest
import com.app.tensquare.ui.otp.OtpVerificationResponse
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.password.PasswordResponse
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel @Inject constructor(private val repository: LoginSignupRepo) :
    ViewModel() {

    private val _response: MutableLiveData<NetworkResult<LoginResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<LoginResponse>> = _response

    fun login(loginRequest: LoginRequest) = viewModelScope.launch(Dispatchers.Main) {
        repository.login(loginRequest).collect { values ->
            _response.value = values
        }
    }

    private val _verifyOtpResponse: MutableLiveData<NetworkResult<OtpVerificationResponse>> =
        MutableLiveData()
    val verifyOtpResponse: LiveData<NetworkResult<OtpVerificationResponse>> = _verifyOtpResponse

    fun verifyOTP(push_token: String, request: OtpRequest) = viewModelScope.launch {
        repository.verifyOTP(push_token, request).collect { values ->
            _verifyOtpResponse.value = values
        }
    }

    private val _resendOtpResponse: MutableLiveData<NetworkResult<LoginResponse>> =
        MutableLiveData()
    val resendOtpResponse: LiveData<NetworkResult<LoginResponse>> = _resendOtpResponse

    fun resendOTP(push_token: String, request: JsonObject) = viewModelScope.launch {
        repository.resendOTP(push_token, request).collect { values ->
            _resendOtpResponse.value = values
        }
    }

    private val _signupResponse: MutableLiveData<NetworkResult<SignupResponse>> = MutableLiveData()
    val signupResponse: LiveData<NetworkResult<SignupResponse>> = _signupResponse

    fun signup(push_token: String, request: SignupRequest) = viewModelScope.launch {
        repository.signup(push_token, request).collect { values ->
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
    }
}