package com.app.tensquare.ui.questionbank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionBankViewModel @Inject constructor(private val repo: QuestionBankRepo) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<TestQuestionList>> = MutableLiveData()
    val response: LiveData<NetworkResult<TestQuestionList>> = _response

    fun getTestQuestionList(testQuestionRequest: TestQuestionRequest) = viewModelScope.launch {
        repo.getTestQuestionList(testQuestionRequest).collect { values ->
            _response.value = values
        }
    }

    private val _testAnswerSubmitResponse: MutableLiveData<NetworkResult<TestAnswerSubmission>> =
        MutableLiveData()
    val testAnswerSubmitResponse: LiveData<NetworkResult<TestAnswerSubmission>> =
        _testAnswerSubmitResponse

    fun submitTestAnswers(request: TestAnswerSubmitRequest) = viewModelScope.launch {
        repo.submitTestAnswers(request).collect { values ->
            _testAnswerSubmitResponse.value = values
        }
    }


    private val _testAnswerResponse: MutableLiveData<NetworkResult<TestAnswerSheet>> =
        MutableLiveData()
    val testAnswerResponse: LiveData<NetworkResult<TestAnswerSheet>> =
        _testAnswerResponse

    fun getTestAnswer(id: String) =
        viewModelScope.launch {
            repo.getTestAnswer(id).collect { values ->
                _testAnswerResponse.value = values
            }
        }

    private val _questionByChapterIdResponse: MutableLiveData<NetworkResult<PracticeQuestionListData>> =
        MutableLiveData()
    val questionByChapterIdResponse: LiveData<NetworkResult<PracticeQuestionListData>> =
        _questionByChapterIdResponse

    fun getPracticeSessionQuestionList(answerSubmissionRequest: PracticeQuestionRequest) =
        viewModelScope.launch {
            repo.getPracticeSessionQuestionList(answerSubmissionRequest).collect { values ->
                _questionByChapterIdResponse.value = values
            }
        }

    private val _practiceTimeSubmissionResponse: MutableLiveData<NetworkResult<String>> =
        MutableLiveData()
    val practiceTimeSubmissionResponse: LiveData<NetworkResult<String>> =
        _practiceTimeSubmissionResponse

    fun submitPracticeSessionTime(request: PracticeSessionTimeRequest) =
        viewModelScope.launch {
            repo.submitPracticeSessionTime(request).collect { values ->
                _practiceTimeSubmissionResponse.value = values
            }
        }

}