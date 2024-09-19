package com.app.tensquare.ui.latestupdate

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LatestUpdateViewModel @Inject constructor(private val repository: LatestUpdateRepo) :
    ViewModel() {

    private val _latestUpdateListResponse: MutableLiveData<NetworkResult<LatestUpdate>> =
        MutableLiveData()
    val latestUpdateListResponse: LiveData<NetworkResult<LatestUpdate>> =
        _latestUpdateListResponse

    fun getLatestUpdateList(pageNo: Int , languageId :String) = viewModelScope.launch {
        repository.getLatestUpdateList(pageNo , languageId).collect { values ->
            _latestUpdateListResponse.value = values
        }
    }

}