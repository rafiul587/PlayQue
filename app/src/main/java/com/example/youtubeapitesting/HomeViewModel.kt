package com.example.youtubeapitesting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubeapitesting.data.PlayListDao
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.PlaylistWithReminder
import com.example.youtubeapitesting.models.Reminder
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
class HomeViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    private val playListDao: PlayListDao
) : ViewModel() {
    private val _playlistInfo = MutableStateFlow<List<PlaylistWithReminder>>(listOf())
    val playlistInfo: StateFlow<List<PlaylistWithReminder>>
        get() = _playlistInfo
    var errorMessage: String by mutableStateOf("")

fun getPlaylistInfo(playListId: String) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val list = mutableListOf<Playlist>()
            val response = apiInterface.getPlaylistInfo(playListId)
            if (response.isSuccessful) {
                response.body()?.get("items")?.asJsonArray?.forEach {
                    val title =
                        it?.asJsonObject?.getAsJsonObject("snippet")?.get("title")?.asString
                            ?: ""
                    val channelTitle = it?.asJsonObject?.getAsJsonObject("snippet")
                        ?.get("channelTitle")?.asString ?: ""
                    val itemCount =
                        it?.asJsonObject?.getAsJsonObject("contentDetails")?.get("itemCount")
                            .toString().toInt()
                    val thumbnailUrl = it.asJsonObject?.getAsJsonObject("snippet")
                        ?.getAsJsonObject("thumbnails")?.getAsJsonObject("standard")?.get("url")
                    thumbnailUrl?.asString?.let { url ->
                        Playlist(
                            id = playListId,
                            title = title,
                            channelTitle = channelTitle,
                            thumbnail = url,
                            itemCount = itemCount
                        )
                    }
                        ?.let { it2 -> list.add(it2) }
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


private fun getPlaylists() = viewModelScope.launch(Dispatchers.IO) {
    playListDao.getAll()
        .collectLatest {
            it.forEach { Log.d("TAG", "getPlaylists: $it") }
            _playlistInfo.value = it
        }
}

fun saveReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
    playListDao.insertReminder(reminder = reminder)
}

init {
    getPlaylists()
}
}