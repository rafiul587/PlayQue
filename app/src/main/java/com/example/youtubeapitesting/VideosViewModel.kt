package com.example.youtubeapitesting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.youtubeapitesting.data.PlayListDao
import com.example.youtubeapitesting.data.VideoDao
import com.example.youtubeapitesting.models.ApiResponse
import com.example.youtubeapitesting.models.Items
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.run

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val apiService: ApiService,
    private val videoDao: VideoDao
) : ViewModel() {
    private val _videos = MutableSharedFlow<PagingData<Video>>()
    var errorMessage: String by mutableStateOf("")
    val videos: Flow<PagingData<Video>>
        get() = _videos

    fun getVideosFromPlaylist(playListId: String, screenId: String) = viewModelScope.launch(Dispatchers.IO){
        Pager(
            pagingSourceFactory = { VideoListPagingSource(apiService, playListId) },
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 20,
            )
        ).flow.collectLatest {
            it.map {
                Log.d("TAG", "vjfhjfjdfhdjfhdfj: ${it.snippet?.resourceId?.videoId}")
                //videoIds.add(it.snippet?.resourceId?.videoId ?: "")
            }
        }

            /*val map = getVideoInfo(videoIds)
            it.map {
                Log.d("TAG", "vjfhjfjdfhdjfhdfj:here22 ${it.snippet?.resourceId?.videoId}")
                val title = it.snippet?.title ?: ""

                val id = it.snippet?.resourceId?.videoId ?: ""
                val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                val durationString = map[id]?.contentDetails?.duration ?: ""
                val duration = Duration.parse(durationString).inWholeSeconds
                val viewCount = map[id]?.statistics?.viewCount ?: ""
                val likeCount = map[id]?.statistics?.likeCount ?: ""
                val video = Video(
                    id = id + "_split_" + playListId,
                    playlistId = playListId,
                    title = title,
                    thumbnail = thumbnailUrl,
                    duration = duration,
                    progress = 0,
                    viewCount = if (viewCount.isEmpty()) "0" else getFormattedNumber(viewCount.toLong()),
                    likeCount = if (likeCount.isEmpty()) "0" else getFormattedNumber(likeCount.toLong())
                )
                video
            }
        }.cachedIn(viewModelScope).collectLatest {
            _videos.emit(it)
        }*/
}
            /*val response = apiService.getVideosFromPlaylist(playListId)
                if (response.isSuccessful) {

                    val videoIds = response.body()?.items?.map {
                        Log.d("TAG", "getVideosFromPlaylist: ${it.snippet?.resourceId}")
                        it.snippet?.resourceId?.videoId ?: ""
                    }

                    val map = videoIds?.let { getVideoInfo(it) } ?: run {
                        errorMessage = "Something went wrong!"
                        return@launch
                    }
                    val nextPageToken = response.body()?.nextPageToken? ?: ""
                    if (map.size == response.body()?.items?.size) {
                        response.body()?.items?.forEach {
                            val title = it.snippet?.title ?: ""

                            val id = it.snippet?.resourceId?.videoId ?: ""
                            val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                            val durationString = map[id]?.contentDetails?.duration ?: ""
                            val duration = Duration.parse(durationString).inWholeSeconds
                            val viewCount = map[id]?.statistics?.viewCount ?: ""
                            val likeCount = map[id]?.statistics?.likeCount ?: ""
                            val video = Video(
                                id = id + "_split_" + playListId,
                                playlistId = playListId,
                                title = title,
                                thumbnail = thumbnailUrl,
                                duration = duration,
                                progress = 0,
                                nextPageToken = null,
                                viewCount = if(viewCount.isEmpty()) "0" else getFormattedNumber(viewCount.toLong()),
                                likeCount = if(likeCount.isEmpty()) "0" else getFormattedNumber(likeCount.toLong())
                            )
                            list.add(video)
                            Log.d("TAG", "getVideosFromPlaylist: $list")
                        }
                        if(screenId == Screens.VideoListScreen.id) {
                            videoDao.insertVideo(list)
                            getVideosByPlaylistId(playListId)
                        }else _videos.value = list
                    }
                } else {
                    errorMessage = response.errorBody().toString()
                }

            } catch (e: Exception) {
                Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
                errorMessage = e.message.toString()
            }
        }*/


    private suspend fun getVideoInfo(videoIds: List<String>): Map<String, Items> {
        val map = mutableMapOf<String, Items>()
        try {
            val response = apiService.getVideo(videoIds)
            if (response.isSuccessful) {
                response.body()?.items?.forEach {
                    val id = it.id ?: ""
                    map[id] = it
                }
            } else {

            }

        } catch (e: Exception) {
            Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
            errorMessage = e.message.toString()
        }
        return map
    }

    private fun getVideosByPlaylistId(id: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TAG", "getPlaylists222: ")
        videoDao.getVideos(id)
            .collectLatest {
                Log.d("TAG", "getPlaylists222: ${it.size}")
                it.forEach { Log.d("TAG", "getPlaylists222: $it") }
                //_videos.value = it
            }
    }

    fun updateVideo(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        videoDao.updateVideo(video = video)
    }
}