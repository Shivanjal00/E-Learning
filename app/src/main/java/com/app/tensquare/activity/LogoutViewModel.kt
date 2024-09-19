package com.app.tensquare.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.login.LoginSignupRepo
import com.app.tensquare.ui.password.NewPasswordRequest
import com.app.tensquare.ui.password.PasswordResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(private val repository: LogoutRepo) :
    ViewModel() {

    private val _logoutResponse: MutableLiveData<NetworkResult<LogoutResponse>> =
        MutableLiveData()
    val logoutResponse: LiveData<NetworkResult<LogoutResponse>> = _logoutResponse

    fun logout() = viewModelScope.launch {
        repository.logout().collect { values ->
            _logoutResponse.value = values
        }
    }

}