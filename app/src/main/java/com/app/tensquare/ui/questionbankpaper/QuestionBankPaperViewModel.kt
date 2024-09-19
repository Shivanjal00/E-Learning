package com.app.tensquare.ui.questionbankpaper

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionBankPaperViewModel  @Inject constructor(private val repository: QuestionBankPaperRepo) :
    ViewModel() {

    private val _questionbankpaperResponse: MutableLiveData<NetworkResult<QuestionBankPaperData>> =
        MutableLiveData()
    val questionbankpaperResponse: LiveData<NetworkResult<QuestionBankPaperData>> =
        _questionbankpaperResponse

    fun getQuestionBankPaperResponseList(id: String,languageId: String, pageNo: Int) = viewModelScope.launch {
        repository.getQuestionBankPaperList(id,languageId, pageNo).collect { values ->
            _questionbankpaperResponse.value = values
        }
    }

}