package com.app.tensquare.ui.pdf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperData
import com.app.tensquare.ui.questionbankpaper.QuestionBankPaperRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(private val repository: PdfViewerRepo) :
    ViewModel() {

    private val _getVerifyUserEnrollmentPlanResponse: MutableLiveData<NetworkResult<PdfViewerData>> =
        MutableLiveData()
    val getVerifyUserEnrollmentPlanResponse: LiveData<NetworkResult<PdfViewerData>> =
        _getVerifyUserEnrollmentPlanResponse

    fun getVerifyUserEnrollmentPlanResponse(subjectId: String, url: String) = viewModelScope.launch {
        repository.getVerifyUserEnrollmentPlan(subjectId, url).collect { values ->
            _getVerifyUserEnrollmentPlanResponse.value = values
        }
    }

}