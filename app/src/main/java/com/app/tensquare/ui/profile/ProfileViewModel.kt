package com.app.tensquare.ui.profile

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepo) :
    ViewModel() {

    private val _profileResponse: MutableLiveData<NetworkResult<ProfileDetailResponse>> =
        MutableLiveData()
    val profileResponse: LiveData<NetworkResult<ProfileDetailResponse>> = _profileResponse

    fun getProfileDetail(token: String) = viewModelScope.launch {
        repository.getProfileDetail(token).collect { values ->
            _profileResponse.value = values
        }
    }

    private val _stateListResponse: MutableLiveData<NetworkResult<StateListResponse>> =
        MutableLiveData()
    val stateListResponse: LiveData<NetworkResult<StateListResponse>> = _stateListResponse

    fun getStateList(languageId: String) = viewModelScope.launch {
        repository.getStateList(languageId).collect { values ->
            _stateListResponse.value = values
        }
    }

    private val _saveProfileDetailResponse: MutableLiveData<NetworkResult<UpdateProfileResponse>> =
        MutableLiveData()
    val saveProfileDetailResponse: LiveData<NetworkResult<UpdateProfileResponse>> =
        _saveProfileDetailResponse

    fun updateProfileDetails(body: MultipartBody) = viewModelScope.launch {
        repository.updateProfileDetails(body).collect { values ->
            _saveProfileDetailResponse.value = values
        }
    }


}