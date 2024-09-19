package com.app.tensquare.ui.notification

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepo) :
    ViewModel() {

    private val _notificationList: MutableLiveData<NetworkResult<NotificationList>> =
        MutableLiveData()
    val notificationList: LiveData<NetworkResult<NotificationList>> = _notificationList

    fun getNotificationList(request: String) = viewModelScope.launch {
        repository.getNotificationList(request).collect { values ->
            _notificationList.value = values
        }
    }

    private val _deleteNotification: MutableLiveData<NetworkResult<NotificationDeleteResponse>> =
        MutableLiveData()
    val deleteNotification: LiveData<NetworkResult<NotificationDeleteResponse>> = _deleteNotification

    fun deleteNotification(request: NotificationDeleteRequest) = viewModelScope.launch {
        repository.deleteNotification(request).collect { values ->
            _deleteNotification.value = values
        }
    }


}