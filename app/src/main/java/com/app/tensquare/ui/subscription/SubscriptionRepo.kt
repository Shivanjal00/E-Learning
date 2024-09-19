package com.app.tensquare.ui.subscription

import com.app.tensquare.network.BaseApiResponse1
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.network.RemoteDataSource
import com.app.tensquare.ui.transaction.CreateOrderData
import com.app.tensquare.ui.transaction.SignVerificationRequest
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class SubscriptionRepo @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    BaseApiResponse1() {

    /*suspend fun getSubscriptionDetails(request: Map<String, String>): Flow<NetworkResult<SubscriptionDataResponse>> {
        return flow<NetworkResult<SubscriptionDataResponse>> {
            emit(safeApiCall { remoteDataSource.getSubscriptionDetails(request) })
        }.flowOn(Dispatchers.IO)
    }*/
    suspend fun getSubscriptionDetails(request: Map<String, String>): Flow<NetworkResult<List<EnrolmentSubject>>> {
        return flow<NetworkResult<List<EnrolmentSubject>>> {
            emit(safeApiCall { remoteDataSource.getSubscriptionDetails(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createOrder(request: String, enrollmentId: String , promoAmount: String ,
                            promoCode: String,finalAmount: String , stateId: String): Flow<NetworkResult<CreateOrderData>> {
        return flow<NetworkResult<CreateOrderData>> {
            emit(safeApiCall { remoteDataSource.createOrder(request, enrollmentId
                ,promoAmount , promoCode , finalAmount , stateId  ) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifySignature(request: SignVerificationRequest): Flow<NetworkResult<CreateOrderData>> {
        return flow<NetworkResult<CreateOrderData>> {
            emit(safeApiCall { remoteDataSource.verifySignature(request) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun applyCoupon(request: String,stateId:String , subjectName: String): Flow<NetworkResult<CouponData>> {
        return flow<NetworkResult<CouponData>> {
            emit(safeApiCall { remoteDataSource.applyCoupon(request) })
        }.flowOn(Dispatchers.IO)
    }

}