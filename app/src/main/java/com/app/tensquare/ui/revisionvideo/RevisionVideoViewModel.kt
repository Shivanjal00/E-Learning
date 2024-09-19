package com.app.tensquare.ui.revisionvideo

import androidx.lifecycle.*
import com.app.tensquare.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevisionVideoViewModel @Inject constructor(private val repository: RevisionVideoRepo) :
    ViewModel() {

    private val _liveData = MutableLiveData<Boolean>()
    private val liveData: LiveData<Boolean> = _liveData

    fun isYoutubeReady() = liveData

    fun setIsYoutubeReady(isReady: Boolean) {
        _liveData.value = isReady
    }

    private val _videoId = MutableLiveData<String>()
    private val videoId: LiveData<String> = _videoId

    fun getVideoId() = videoId

    fun setVideoId(videoId: String) {
        _videoId.value = videoId
    }

    private val _revisionVideoListResponse: MutableLiveData<NetworkResult<VideoListData>> =
        MutableLiveData()
    val revisionVideoListResponse: LiveData<NetworkResult<VideoListData>> =
        _revisionVideoListResponse

    fun getRevisionVideoList(request: Map<String, String>) = viewModelScope.launch {
        repository.getRevisionVideoList(request).collect { values ->
            _revisionVideoListResponse.value = values
        }
    }

    private val _revVideoDetailResponse: MutableLiveData<NetworkResult<VideoDetailData>> =
        MutableLiveData()
    val revVideoDetailResponse: LiveData<NetworkResult<VideoDetailData>> =
        _revVideoDetailResponse

    fun getRevisionVideoDetail(videoId: String) = viewModelScope.launch {
        repository.getRevisionVideoDetail(videoId).collect { values ->
            _revVideoDetailResponse.value = values
        }
    }

 private val _searchedVideoDetailResponse: MutableLiveData<NetworkResult<SearchedVideoDetail>> =
        MutableLiveData()
    val searchedVideoDetailResponse: LiveData<NetworkResult<SearchedVideoDetail>> =
        _searchedVideoDetailResponse

    fun geSearchedtRevisionVideoDetail(videoId: String) = viewModelScope.launch {
        repository.geSearchedtRevisionVideoDetail(videoId).collect { values ->
            _searchedVideoDetailResponse.value = values
        }
    }

    private val _latestUpdateDetailResponse: MutableLiveData<NetworkResult<NewUpdate>> =
        MutableLiveData()
    val latestVideoDetailResponse: LiveData<NetworkResult<NewUpdate>> =
        _latestUpdateDetailResponse

    fun getLatestUpdateDetail(id: String) = viewModelScope.launch {
        repository.getLatestUpdateDetail(id).collect { values ->
            _latestUpdateDetailResponse.value = values
        }
    }

    private val _videoLikeResponse: MutableLiveData<NetworkResult<Data1>> =
        MutableLiveData()
    val videoLikeResponse: LiveData<NetworkResult<Data1>> =
        _videoLikeResponse

    fun doLikeVideo(request: LikeVideoRequest) = viewModelScope.launch {
        repository.doLikeVideo(request).collect { values ->
            _videoLikeResponse.value = values
        }
    }

}