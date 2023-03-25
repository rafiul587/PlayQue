package com.example.youtubeapitesting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubeapitesting.data.PlayListDao
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    private val playListDao: PlayListDao
) : ViewModel() {
    private val _videos = MutableStateFlow<List<Video>>(listOf())
    var errorMessage: String by mutableStateOf("")
    val videos: StateFlow<List<Video>>
        get() = _videos

    fun getVideosFromPlaylist(playListId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = mutableListOf<Video>()
                val response = apiInterface.getVideosFromPlaylist(playListId)
                if (response.isSuccessful) {
                    val videoIds = response.body()?.get("items")?.asJsonArray?.map {
                        it?.asJsonObject?.getAsJsonObject("snippet")?.getAsJsonObject("resourceId")
                            ?.get("videoId")?.asString ?: ""
                    }
                    val map = mutableMapOf<String, Long>()
                    val videoResponse =
                        if (videoIds != null && videoIds.isNotEmpty()) apiInterface.getVideoInfo(
                            videoIds
                        ) else null
                    videoResponse?.body()?.get("items")?.asJsonArray?.forEach {
                        val duration = it?.asJsonObject?.getAsJsonObject("contentDetails")
                            ?.get("duration")?.asString ?: ""
                        val id = it?.asJsonObject?.get("id")?.asString ?: ""
                        val s = Duration.parse(duration).inWholeSeconds
                        map[id] = s
                    }
                    response.body()?.get("items")?.asJsonArray?.forEach {
                        val title =
                            it?.asJsonObject?.getAsJsonObject("snippet")?.get("title").toString()
                        val id = it?.asJsonObject?.getAsJsonObject("snippet")
                            ?.getAsJsonObject("resourceId")?.get("videoId")?.asString ?: ""
                        val thumbnailUrl = it.asJsonObject?.getAsJsonObject("snippet")
                            ?.getAsJsonObject("thumbnails")?.getAsJsonObject("standard")?.get("url")
                        val duration = map[id] ?: 0L
                        thumbnailUrl?.asString?.let { url ->
                            Video(
                                id = id,
                                playlistId = playListId,
                                title = title,
                                thumbnail = url,
                                duration = duration,
                                progress = 0
                            )
                        }?.let { it2 -> list.add(it2) }
                        Log.d("TAG", "getVideosFromPlaylist: $id")
                    }
                    playListDao.insertVideo(list)
                    getVideosByPlaylistId(playListId)
                } else {
                    Log.d("TAG", "getVideosFromPlaylist: ${response.raw().toString()}")
                }

            } catch (e: Exception) {
                Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
                errorMessage = e.message.toString()
            }
        }
    }

    fun getVideoInfo(videoIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = mutableListOf<Playlist>()
                val response = apiInterface.getVideoInfo(videoIds)
                if (response.isSuccessful) {
                    response.body()?.get("items")?.asJsonArray?.forEach {
                        val title =
                            it?.asJsonObject?.getAsJsonObject("snippet")?.get("title")?.asString
                                ?: ""
                        val channelTitle = it?.asJsonObject?.getAsJsonObject("snippet")
                            ?.get("channelTitle")?.asString ?: ""
                        val duration =
                            it?.asJsonObject?.getAsJsonObject("contentDetails")?.get("duration")
                                .toString().toInt()
                    }
                    if (list.isNotEmpty()) {
                        Log.d("TAG", "getPlaylistInfo:${list.first()} ")
                        playListDao.insertAll(list.first())
                    }
                } else {
                    Log.d("TAG", "getVideosFromPlaylist: ${response.raw().toString()}")
                }

            } catch (e: Exception) {
                Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
                errorMessage = e.message.toString()
            }
        }
    }

    private fun getVideosByPlaylistId(id: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TAG", "getPlaylists222: ")
        playListDao.getVideos(id)
            .collectLatest {
                Log.d("TAG", "getPlaylists222: ${it.size}")
                it.forEach { Log.d("TAG", "getPlaylists222: $it") }
                _videos.value = it
            }
    }

    fun updateVideo(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.updateVideo(video = video)
    }
}