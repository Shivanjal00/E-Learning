package com.app.tensquare.ui.analysis

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(private val repository: AnalysisRepo) :
    ViewModel() {

    private val _subjectAnalysisResponse: MutableLiveData<NetworkResult<SubjectAnalysisResponse>> =
        MutableLiveData()
    val subjectAnalysisResponse: LiveData<NetworkResult<SubjectAnalysisResponse>> =
        _subjectAnalysisResponse

    fun getSubjectWiseAnalysis(subjectId: String) = viewModelScope.launch {
        repository.getSubjectWiseAnalysis(subjectId).collect { values ->
            _subjectAnalysisResponse.value = values
        }
    }

    private val _subjectWiseChapterAnalysisListResponse: MutableLiveData<NetworkResult<ChapterAnalysisListResponse>> =
        MutableLiveData()
    val subjectWiseChapterAnalysisListResponse: LiveData<NetworkResult<ChapterAnalysisListResponse>> =
        _subjectWiseChapterAnalysisListResponse

    fun getSubjectWiseChapterAnalysisList(subjectId: String) = viewModelScope.launch {
        repository.getSubjectWiseChapterAnalysisList(subjectId).collect { values ->
            _subjectWiseChapterAnalysisListResponse.value = values
        }
    }

    private val _chapterAnalysisResponse: MutableLiveData<NetworkResult<ChapterAnalysisResponse>> =
        MutableLiveData()
    val chapterAnalysisResponse: LiveData<NetworkResult<ChapterAnalysisResponse>> =
        _chapterAnalysisResponse

    fun getChapterWiseAnalysis(id: String) = viewModelScope.launch {
        repository.getChapterWiseAnalysis(id).collect { values ->
            _chapterAnalysisResponse.value = values
        }
    }

    /* private val _createOrder: MutableLiveData<NetworkResult<CreateOrderResponse>> =
         MutableLiveData()
     val createOrder: LiveData<NetworkResult<CreateOrderResponse>> = _createOrder

     fun createOrder(request: String) = viewModelScope.launch {
         repository.createOrder(request).collect { values ->
             _createOrder.value = values
         }
     }

     private val _verifySignature: MutableLiveData<NetworkResult<CreateOrderResponse>> =
         MutableLiveData()
     val verifySignature: LiveData<NetworkResult<CreateOrderResponse>> = _verifySignature

     fun verifySignature(request: SignVerificationRequest) = viewModelScope.launch {
         repository.verifySignature(request).collect { values ->
             _verifySignature.value = values
         }
     }

     private val _applyCouponResponse: MutableLiveData<NetworkResult<CouponApplicationRequest>> =
         MutableLiveData()
     val applyCouponResponse: LiveData<NetworkResult<CouponApplicationRequest>> =
         _applyCouponResponse

     fun applyCoupon(request: String) = viewModelScope.launch {
         repository.applyCoupon(request).collect { values ->
             _applyCouponResponse.value = values
         }
     }*/


}