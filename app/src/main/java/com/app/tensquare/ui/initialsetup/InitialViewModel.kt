package com.app.tensquare.ui.initialsetup

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.response.RefreshTokenData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(private val repository: InitialDataRepo) :
    ViewModel() {

    private val _guestTokenResponse: MutableLiveData<NetworkResult<GuestToken>> =
        MutableLiveData()
    val guestTokenResponse: LiveData<NetworkResult<GuestToken>> = _guestTokenResponse

    fun getGuestToken(deviceId: String) = viewModelScope.launch {
        repository.getGuestToken(deviceId).collect { values ->
            _guestTokenResponse.value = values
        }
    }

    private val _languageListResponse: MutableLiveData<NetworkResult<List<Language>>> =
        MutableLiveData()
    val languageListResponse: LiveData<NetworkResult<List<Language>>> = _languageListResponse

    fun getLanguageList() = viewModelScope.launch {
        repository.getLanguageList().collect { values ->
            _languageListResponse.value = values
        }
    }

    private val _classListResponse: MutableLiveData<NetworkResult<ClassListResponse>> =
        MutableLiveData()
    val classListResponse: LiveData<NetworkResult<ClassListResponse>> = _classListResponse

    fun getClassList(languageId: String) = viewModelScope.launch {
        repository.getClassList(languageId).collect { values ->
            _classListResponse.value = values
        }
    }

    private val _subjectListResponse: MutableLiveData<NetworkResult<List<SubjectData>>> =
        MutableLiveData()
    val subjectListResponse: LiveData<NetworkResult<List<SubjectData>>> = _subjectListResponse

    fun getSubjectList(languageId: String) = viewModelScope.launch {
        repository.getSubjectList(languageId).collect { values ->
            _subjectListResponse.value = values
        }
    }

    private val _refreshTokenResponse: MutableLiveData<NetworkResult<RefreshTokenData>> =
        MutableLiveData()
    val refreshTokenResponse: LiveData<NetworkResult<RefreshTokenData>> = _refreshTokenResponse

    fun getRefreshToken(token: String) = viewModelScope.launch {
        repository.getRefreshToken(token).collect { values ->
            _refreshTokenResponse.value = values
        }
    }

}