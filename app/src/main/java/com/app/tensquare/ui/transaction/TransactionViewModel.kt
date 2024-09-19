package com.app.tensquare.ui.transaction

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val repository: TransactionRepo) :
    ViewModel() {

    private val _txnList: MutableLiveData<NetworkResult<TxnListResponse>> =
        MutableLiveData()
    val txnList: LiveData<NetworkResult<TxnListResponse>> = _txnList

    fun getTxnList() = viewModelScope.launch {
        repository.getTxnList().collect { values ->
            _txnList.value = values
        }
    }


}