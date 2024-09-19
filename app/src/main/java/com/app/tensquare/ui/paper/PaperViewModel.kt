package com.app.tensquare.ui.paper

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaperViewModel @Inject constructor(private val repository: PaperRepo) :
    ViewModel() {

    private val _modelPaperListResponse: MutableLiveData<NetworkResult<ModelPaperData>> =
        MutableLiveData()
    val modelPaperListResponse: LiveData<NetworkResult<ModelPaperData>> =
        _modelPaperListResponse

    fun getModelPaperList(id: String, pageNo: Int, languageId: String) = viewModelScope.launch {
        repository.getModelPaperList(id, pageNo, languageId).collect { values ->
            _modelPaperListResponse.value = values
        }
    }

    private val _homeModelPaperListResponse: MutableLiveData<NetworkResult<ModelPaperData>> =
        MutableLiveData()
    val homeModelPaperListResponse: LiveData<NetworkResult<ModelPaperData>> =
        _homeModelPaperListResponse

    fun getHomeModelPaperList(pageNo: Int , languageId: String) = viewModelScope.launch {
        repository.getHomeModelPaperList(pageNo , languageId).collect { values ->
            _homeModelPaperListResponse.value = values
        }
    }

    private val _previousYearPaperResponse: MutableLiveData<NetworkResult<PreviousYearPaperData>> =
        MutableLiveData()
    val previousYearPaperResponse: LiveData<NetworkResult<PreviousYearPaperData>> =
        _previousYearPaperResponse

    fun getPreviousYearPaperList(id: String, pageNo: Int, languageId: String) = viewModelScope.launch {
        repository.getPreviousYearPaperList(id, pageNo,languageId).collect { values ->
            _previousYearPaperResponse.value = values
        }
    }

    private val _homePreviousYearPaperResponse: MutableLiveData<NetworkResult<PreviousYearPaperData>> =
        MutableLiveData()
    val homePreviousYearPaperResponse: LiveData<NetworkResult<PreviousYearPaperData>> =
        _homePreviousYearPaperResponse

    fun getHomePreviousYearPaperList(pageNo: Int , languageId: String) = viewModelScope.launch {
        repository.getHomePreviousYearPaperList(pageNo, languageId).collect { values ->
            _homePreviousYearPaperResponse.value = values
        }
    }


}