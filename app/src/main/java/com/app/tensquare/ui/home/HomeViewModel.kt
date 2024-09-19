package com.app.tensquare.ui.home

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import com.app.tensquare.response.RefreshTokenData
import com.app.tensquare.ui.initialsetup.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeDataRepo) :
    ViewModel() {

    private val _homeDataResponse: MutableLiveData<NetworkResult<HomeData>> =
        MutableLiveData()
    val homeDataResponse: LiveData<NetworkResult<HomeData>> = _homeDataResponse

    fun getHomeData(token: String, request: HomeDataRequest) = viewModelScope.launch {
        repository.getHomeData(token, request).collect { values ->
            _homeDataResponse.value = values
        }
    }

    private val _refreshTokenResponse: MutableLiveData<NetworkResult<RefreshTokenData>> =
        MutableLiveData()
    val refreshTokenResponse: LiveData<NetworkResult<RefreshTokenData>> = _refreshTokenResponse

    fun getRefreshToken(token: String) = viewModelScope.launch {
        repository.getRefreshToken(token).collect { values ->
            _refreshTokenResponse.value = values
        }
    }

    private val _searchResponse: MutableLiveData<NetworkResult<SearchData>> =
        MutableLiveData()
    val searchResponse: LiveData<NetworkResult<SearchData>> = _searchResponse

    fun searchSomething(searchRequest: SearchRequest) = viewModelScope.launch {
        repository.searchSomething(searchRequest).collect { values ->
            _searchResponse.value = values
        }
    }

}