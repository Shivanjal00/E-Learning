package com.app.tensquare.ui.instruction

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstructionViewModel @Inject constructor(private val repository: InstructionRepo) :
    ViewModel() {

    private val _instructionListResponse: MutableLiveData<NetworkResult<InstructionData>> =
        MutableLiveData()
    val instructionListResponse: LiveData<NetworkResult<InstructionData>> =
        _instructionListResponse

    fun getInstructionList(request: HashMap<String, String>) = viewModelScope.launch {
        repository.getInstructionList(request).collect { values ->
            _instructionListResponse.value = values
        }
    }


}