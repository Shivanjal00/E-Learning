package com.app.tensquare.ui.session

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(private val repository: SessionVideoRepo) :
    ViewModel() {

    private val _practiceSessionListResponse: MutableLiveData<NetworkResult<PracticeSessionListResponse>> =
        MutableLiveData()
    val practiceSessionListResponse: LiveData<NetworkResult<PracticeSessionListResponse>> =
        _practiceSessionListResponse

    fun getPracticeSessionList(id: String) = viewModelScope.launch {
        repository.getPracticeSessionList(id).collect { values ->
            _practiceSessionListResponse.value = values
        }
    }

//    private val _revVideoDetailResponse: MutableLiveData<NetworkResult<RevisionVideoDetailResponse>> =
//        MutableLiveData()
//    val revVideoDetailResponse: LiveData<NetworkResult<RevisionVideoDetailResponse>> = _revVideoDetailResponse
//
//    fun getRevisionVideoDetail(videoId: String) = viewModelScope.launch {
//        repository.getRevisionVideoDetail(videoId).collect { values ->
//            _revVideoDetailResponse.value = values
//        }
//    }

}