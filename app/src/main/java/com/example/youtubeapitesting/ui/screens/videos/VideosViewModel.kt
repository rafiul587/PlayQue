package com.example.youtubeapitesting.ui.screens.videos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.youtubeapitesting.data.local.AppDatabase
import com.example.youtubeapitesting.data.local.RemoteKey
import com.example.youtubeapitesting.data.local.VideoDao
import com.example.youtubeapitesting.data.remote.ApiService
import com.example.youtubeapitesting.data.remote.sources.VideoListRemoteMediator
import com.example.youtubeapitesting.models.ApiResponse
import com.example.youtubeapitesting.models.Items
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.ui.screens.search.getFormattedNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val videoDao: VideoDao
) : ViewModel() {
    /*private val _videos = MutableSharedFlow<PagingData<Video>>()
    var errorMessage: String by mutableStateOf("")
    val videos: Flow<PagingData<Video>>
        get() = _videos
    var state by mutableStateOf(ScreenState())

    private val repository = com.example.youtubeapitesting.ui.screens.videos.VideoListPagingSource(apiService)
    var paginator: DefaultPaginator<Int,ApiResponse>? = null*/

    private var currentPlaylistId: String = ""

    private val _videos = MutableSharedFlow<PagingData<Video>>()
    val videos: Flow<PagingData<Video>> = _videos

    @OptIn(ExperimentalPagingApi::class)
    fun getVideosFromPlaylist(playlistId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _videos.emitAll(
                Pager(
                    config = PagingConfig(pageSize = 10),
                    remoteMediator = VideoListRemoteMediator(
                        apiCall = { apiService.getVideosFromPlaylist(playlistId, it) },
                        map = { body: ApiResponse ->
                            val items = body.items.filter { it.status?.privacyStatus == "public" }

                            val videoIds =
                                items.mapNotNull { item -> item.snippet?.resourceId?.videoId }
                            val map = getVideoInfo(videoIds)
                            appDatabase.remoteKeyDao().insertOrReplace(
                                RemoteKey(
                                    playlistId,
                                    body.nextPageToken,
                                    body.prevPageToken
                                )
                            )
                            val videos = items.map {
                                val title = it.snippet?.title ?: ""
                                val id = it.snippet?.resourceId?.videoId ?: ""


                                val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                                val durationString = map[id]?.contentDetails?.duration ?: ""
                                val duration = Duration.parse(durationString).inWholeSeconds
                                val viewCount = map[id]?.statistics?.viewCount ?: ""
                                val likeCount = map[id]?.statistics?.likeCount ?: ""
                                val video = Video(
                                    id = id + "_split_" + playlistId,
                                    playlistId = playlistId,
                                    title = title,
                                    thumbnail = thumbnailUrl,
                                    duration = duration,
                                    progress = 0,
                                    viewCount = if (viewCount.isEmpty()) "0" else getFormattedNumber(
                                        viewCount.toLong()
                                    ),
                                    likeCount = if (likeCount.isEmpty()) "0" else getFormattedNumber(
                                        likeCount.toLong()
                                    )
                                )

                                video
                            }

                            withContext(Dispatchers.IO) {
                                videoDao.insertVideo(videos)
                            }
                            videos
                        },
                        playlistId = playlistId,
                        database = appDatabase,
                    )
                ) {
                    videoDao.getVideos(playlistId)
                }.flow
            )
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


    private fun getVideosByPlaylistId(id: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TAG", "getPlaylists222: ")
        Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = {
            videoDao.getVideos(id)
        }).flow.cachedIn(viewModelScope).collectLatest {
            it.map { Log.d("TAG", "getPlaylists222: $it") }
            _videos.emit(it)
        }
    }


    fun updateVideo(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        videoDao.updateVideo(video = video)
    }

    private suspend fun getVideoInfo(videoIds: List<String>): Map<String, Items> {
        val map = mutableMapOf<String, Items>()
        try {
            Log.d("TAG", "getVideoInfo: ${videoIds}")
            val response = apiService.getVideo(videoIds)
            if (response.isSuccessful) {
                response.body()?.items?.forEach {
                    Log.d("TAG", "getVideoInfo: ${it}")
                    val id = it.id ?: ""
                    map[id] = it
                }
            } else {

            }

        } catch (e: Exception) {
            Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
            //errorMessage = e.message.toString()
        }
        return map
    }

}