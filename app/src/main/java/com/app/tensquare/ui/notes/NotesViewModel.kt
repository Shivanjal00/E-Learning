package com.app.tensquare.ui.notes

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepo) :
    ViewModel() {

    private val _notesListResponse: MutableLiveData<NetworkResult<NotesListResponse>> =
        MutableLiveData()
    val notesListResponse: LiveData<NetworkResult<NotesListResponse>> =
        _notesListResponse

    fun getNotesList(request: Map<String, String>) = viewModelScope.launch {
        repository.getNotesList(request).collect { values ->
            _notesListResponse.value = values
        }
    }

    /*private val _previousYearPaperResponse: MutableLiveData<NetworkResult<PreviousYearPaperResponse>> =
        MutableLiveData()
    val previousYearPaperResponse: LiveData<NetworkResult<PreviousYearPaperResponse>> = _previousYearPaperResponse

    fun getPreviousYearPaperList(id: String, pageNo: Int) = viewModelScope.launch {
        repository.getPreviousYearPaperList(id,pageNo).collect { values ->
            _previousYearPaperResponse.value = values
        }
    }*/

}