package com.app.tensquare.ui.subscription

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.ui.transaction.SignVerificationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val repository: SubscriptionRepo) :
    ViewModel() {

    /*private val _subscriptionDetails: MutableLiveData<NetworkResult<SubscriptionDataResponse>> =
        MutableLiveData()
    val subscriptionDetails: LiveData<NetworkResult<SubscriptionDataResponse>> =
        _subscriptionDetails

    fun getSubscriptionDetails(request: Map<String, String>) = viewModelScope.launch {
        repository.getSubscriptionDetails(request).collect { values ->
            _subscriptionDetails.value = values
        }
    }*/
    private val _subscriptionDetails: MutableLiveData<NetworkResult<List<EnrolmentSubject>>> =
        MutableLiveData()
    val subscriptionDetails: LiveData<NetworkResult<List<EnrolmentSubject>>> =
        _subscriptionDetails

    fun getSubscriptionDetails(request: Map<String, String>) = viewModelScope.launch {
        repository.getSubscriptionDetails(request).collect { values ->
            _subscriptionDetails.value = values
        }
    }

    private val _createOrder: MutableLiveData<NetworkResult<CreateOrderData>> =
        MutableLiveData()
    val createOrder: LiveData<NetworkResult<CreateOrderData>> = _createOrder

    fun createOrder(request: String, enrollmentId: String ,promoAmount: String ,
                    promoCode: String,finalAmount: String , stateId: String) = viewModelScope.launch {
        repository.createOrder(request, enrollmentId , promoAmount , promoCode , finalAmount, stateId).collect { values ->
            _createOrder.value = values
        }
    }

    private val _verifySignature: MutableLiveData<NetworkResult<CreateOrderData>> =
        MutableLiveData()
    val verifySignature: LiveData<NetworkResult<CreateOrderData>> = _verifySignature

    fun verifySignature(request: SignVerificationRequest) = viewModelScope.launch {
        repository.verifySignature(request).collect { values ->
            _verifySignature.value = values
        }
    }

    private val _applyCouponResponse: MutableLiveData<NetworkResult<CouponData>> =
        MutableLiveData()
    val applyCouponResponse: LiveData<NetworkResult<CouponData>> =
        _applyCouponResponse

    fun applyCoupon(request: String, stateId: String , subjectName: String) = viewModelScope.launch {
        repository.applyCoupon(request, stateId , subjectName).collect { values ->
            _applyCouponResponse.value = values
        }
    }


}