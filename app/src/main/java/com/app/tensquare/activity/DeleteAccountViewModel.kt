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
class DeleteAccountViewModel @Inject constructor(private val repository: DeleteAccountRepo) :
    ViewModel() {

    private val _deleteAccountResponse: MutableLiveData<NetworkResult<DeleteAccountResponse>> =
        MutableLiveData()
    val deleteAccountResponse: LiveData<NetworkResult<DeleteAccountResponse>> = _deleteAccountResponse

    fun deleteAccount() = viewModelScope.launch {
        repository.deleteAccount().collect { values ->
            _deleteAccountResponse.value = values
        }
    }

}