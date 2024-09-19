package com.app.tensquare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateLanguageViewModel @Inject constructor(private val repository: UpdateLanguageRepo) :
    ViewModel() {

    private val _updateLanguage: MutableLiveData<NetworkResult<UpdateLanguageResponse>> =
        MutableLiveData()
    val updateLanguage: LiveData<NetworkResult<UpdateLanguageResponse>> = _updateLanguage

    fun updateLanguage(request: UpdateLanguageRequest) = viewModelScope.launch {
        repository.updateLanguage(request).collect { values ->
            _updateLanguage.value = values
        }
    }

}