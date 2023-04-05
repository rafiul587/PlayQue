package com.example.youtubeapitesting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.youtubeapitesting.data.PlayListDao
import com.example.youtubeapitesting.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.pow

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    private val playListDao: PlayListDao
) : ViewModel() {
    private val _channelLists = MutableStateFlow<List<Channel>>(listOf())
    val channelLists: StateFlow<List<Channel>>
        get() = _channelLists

    private val _playlists = MutableStateFlow<List<Playlist>>(listOf())
    val playlists: StateFlow<List<Playlist>>
        get() = _playlists




    var errorMessage: String by mutableStateOf("")

    var selectedType by mutableStateOf(0)
    private set

    fun search(query: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TAG", "search: ")
                val response = apiService.search(query = query, type = type)
                if (response.isSuccessful) {

                    if (type == "channel") {
                        val results = getChannelResults(response)
                        Log.d("TAG", "search: $results")
                        if (results.isEmpty()) {

                            errorMessage = "Something went wrong!"
                        } else {

                            _channelLists.value = results
                        }
                    } else {
                        val results = getPlaylistResults(response)
                        if (results.isEmpty()) {
                            errorMessage = "Something went wrong!"
                        } else {
                            _playlists.value = results
                        }
                    }
                } else {
                    errorMessage = response.message().toString()
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    private suspend fun getChannelResults(response: Response<SearchResponse>): List<Channel> {
        val list = mutableListOf<Channel>()
        val channelIds = response.body()?.items?.map {
            it.id?.channelId ?: ""
        }

        val map = channelIds?.let { getChannelStat(it) } ?: run {
            return list
        }

        if (map.size == response.body()?.items?.size) {
            response.body()?.items?.forEach {
                val id = it.id?.channelId ?: ""
                val title =
                    it.snippet?.title ?: ""
                val isSubCountHidden = map[id]?.hiddenSubscriberCount ?: false
                val numbSub =
                    if (isSubCountHidden) "Hidden" else map[id]?.subscriberCount
                        ?: "0"
                val numbVideos = map[id]?.videoCount ?: ""
                val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                val channel = Channel(
                    id = id,
                    title = title,
                    thumbnail = thumbnailUrl,
                    numbSub = getFormattedNumber(numbSub.toLong()),
                    numbVideos = numbVideos,
                )
                list.add(channel)
            }
        }
        Log.d("TAG", "getChannelResults: ${list.size}")
        return list
    }

    private suspend fun getPlaylistResults(response: Response<SearchResponse>): List<Playlist> {
        val list = mutableListOf<Playlist>()
        val playlistIds = response.body()?.items?.map {
            it.id?.playlistId ?: ""
        }
        Log.d("TAG", "getPlaylistResults: ${playlistIds?.size}")
        val map = playlistIds?.let { getPlaylistVideoCount(it) } ?: run {
            return list
        }
        if (map.size == response.body()?.items?.size) {
            response.body()?.items?.forEach {
                val id = it.id?.playlistId ?: ""
                val title = it.snippet?.title ?: ""
                val channelTitle = it.snippet?.channelTitle ?: ""
                val itemCount = map[id] ?: 0
                val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                val playlist = Playlist(
                    id = id,
                    title = title,
                    thumbnail = thumbnailUrl,
                    channelTitle = channelTitle,
                    itemCount = itemCount,
                )
                list.add(playlist)
            }
        }
        return list
    }

    fun getPlaylistByChannelId(channelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getPlaylistByChannelId(channelId = channelId)
                if (response.isSuccessful) {
                    Log.d("TAG", "search999: ${response.body()?.items?.size}")
                    val list = mutableListOf<Playlist>()
                    val playlistIds = response.body()?.items?.map {
                        it.id ?: ""
                    }
                    val map = playlistIds?.let { getPlaylistVideoCount(it) } ?: run {
                        return@launch
                    }
                    Log.d("TAG", "getPlaylistByChannelId: ${playlistIds}, ${response.body()?.items?.size}")
                    if (map.size == response.body()?.items?.size) {
                        response.body()?.items?.forEach {
                            val id = it.id ?: ""
                            val title =
                                it.snippet?.title ?: ""
                            val channelTitle = it.snippet?.channelTitle ?: ""
                            val itemCount = map[id] ?: 0
                            val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                            Log.d("TAG", "getPlaylistByChannelId2222: ")
                            val playlist = Playlist(
                                id = id,
                                title = title,
                                channelTitle = channelTitle,
                                thumbnail = thumbnailUrl,
                                itemCount = itemCount
                            )
                            Log.d("TAG", "getPlaylistByChannelId22222222: $playlist")
                            list.add(playlist)
                        }
                        _playlists.value = list

                    } else {
                        Log.d("TAG", "getPlaylistByChannelId: ${response.raw()}")
                        errorMessage = response.message().toString()
                    }
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()

            }
        }
    }

    private suspend fun getChannelStat(channelIds: List<String>): Map<String, Statistics> {
        val map = mutableMapOf<String, Statistics>()
        try {
            val response = apiService.getChannelStats(channelIds)
            if (response.isSuccessful) {
                response.body()?.items?.forEach {
                    val statistics = it.statistics!!
                    val id = it.id ?: ""
                    Log.d("TAG", "getChannelStat: ${it.id}")
                    map[id] = statistics
                }
            } else {

            }

        } catch (e: Exception) {
            Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
            errorMessage = e.message.toString()
        }
        return map
    }

    private suspend fun getPlaylistVideoCount(playlistIds: List<String>): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        try {
            val response = apiService.getPlaylistVideoCount(playlistIds)

            if (response.isSuccessful) {
                Log.d("TAG", "getPlaylistVideoCount: ${response.body()?.items?.size}")
                response.body()?.items?.forEach {
                    val itemCount = it.contentDetails?.itemCount ?: 0
                    val id = it.id ?: ""
                    Log.d("TAG", "getPlaylistVideoCount: ${it.id}, $itemCount")
                    map[id] = itemCount
                }
            } else {
                Log.d("TAG", "getVideosFromPlaylist: ${response.raw()}")
            }

        } catch (e: Exception) {
            Log.d("TAG", "getVideosFromPlaylist: ${e.message}")
            errorMessage = e.message.toString()
        }
        Log.d("TAG", "getPlaylistByChannelId333333333: ${map.size}")
        return map
    }

    fun addNewPlaylist(playlist: Playlist) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.insertAll(playlist)
    }

    fun selectType(value: Int){
        selectedType = value
    }
}

fun getFormattedNumber(count: Long): String {
    if (count < 1000) return "" + count
    val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
    val decimal = DecimalFormat("#.##").format(count / 1000.0.pow(exp.toDouble()))
    return String.format("%s%c", decimal, "kMGTPE"[exp - 1])
}