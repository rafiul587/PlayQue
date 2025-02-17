package com.qubartech.playque.ui.screens.search

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.qubartech.playque.R
import com.qubartech.playque.data.local.PlayListDao
import com.qubartech.playque.data.remote.ApiService
import com.qubartech.playque.data.remote.sources.PlaylistByChannelPagingSource
import com.qubartech.playque.data.remote.sources.SearchChannelPagingSource
import com.qubartech.playque.data.remote.sources.SearchPlaylistPagingSource
import com.qubartech.playque.data.remote.sources.SearchedVideoPagingSource
import com.qubartech.playque.models.*
import com.qubartech.playque.utils.Constants.CHANNEL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.pow
import kotlin.time.Duration

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    private val playListDao: PlayListDao
) : ViewModel() {

    private val _channelLists = MutableStateFlow<PagingData<Channel>>(
        emptyData()
    )
    val channelLists: Flow<PagingData<Channel>>
        get() = _channelLists

    private val _playlists = MutableStateFlow<PagingData<Playlist>>(emptyData())
    val playlists: Flow<PagingData<Playlist>>
        get() = _playlists

    private val _videos = MutableSharedFlow<PagingData<Video>>()
    val videos: Flow<PagingData<Video>> = _videos

    var query by mutableStateOf("")
        private set

    var errorMessage: String by mutableStateOf("")

    var selectedType by mutableStateOf(0)
        private set

    private fun getChannelPager() = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        )
    ) {
        SearchChannelPagingSource(apiCall = {
            apiService.search(
                query,
                "channel",
                it
            )
        }, query) {
            getChannelResults(it)
        }
    }.flow.cachedIn(viewModelScope)

    private fun getPlaylistPager() = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        )
    ) {
        SearchPlaylistPagingSource(apiCall = {
            apiService.search(
                query,
                "playlist",
                it
            )
        }) {

            getPlaylistResults(it)
        }
    }.flow.cachedIn(viewModelScope)

    private fun <T : Any> emptyData(): PagingData<T> = PagingData.empty(
        LoadStates(
            LoadState.NotLoading(false),
            LoadState.NotLoading(false),
            LoadState.NotLoading(false)
        )
    )


    /*fun setChannelSearch(query: String) {
        _channelQuery.value = query
    }

    fun setPlaylistSearch(query: String) {
        _playlistQuery.value = query
    }*/

    fun search(type: Int) {
        selectedType = type
        if (query.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                if (type == CHANNEL) {
                    _channelLists.emitAll(getChannelPager())
                } else {
                    _playlists.emitAll(getPlaylistPager())
                }
            }
        }
    }
    /*            try {

                    Log.d("TAG", "search: ")
                    val response = apiService.search(query = query,
                        type = type,
                        "")
                    if (response.isSuccessful) {

                        if (selectedType == CHANNEL) {
                            val results = getChannelResults(response.body()!!.items)
                            Log.d("TAG", "search: $results")
                            if (results.isEmpty()) {

                                errorMessage = "Something went wrong!"
                            } else {

                                _channelLists.value = results
                            }
                        } else {
                            val results = getPlaylistResults(response.body()!!.items)
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
        }*/

    private suspend fun getChannelResults(items: List<SearchResponse.Items>): List<Channel> {
        val list = mutableListOf<Channel>()

        val channelIds = items.mapNotNull { it.id?.channelId }

        val map = getChannelStat(channelIds)

        if (map.size == items.size) {
            items.forEach {
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

    fun getVideosFromPlaylist(playlistId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _videos.emitAll(
                Pager(
                    config = PagingConfig(pageSize = 10),
                ) {
                    SearchedVideoPagingSource(
                        apiCall = {
                            apiService.getVideosFromPlaylist(
                                playlistId = playlistId,
                                pageToken = it
                            )
                        },
                        map = { body: ApiResponse ->
                            val items = body.items.filter { it.status?.privacyStatus == "public" }
                            val videoIds =
                                items.mapNotNull { item -> item.snippet?.resourceId?.videoId }
                            val map = getVideoInfo(videoIds)
                            val videos = items.map {
                                val title = it.snippet?.title ?: ""
                                val videoPublishedAt = it.contentDetails?.videoPublishedAt ?: ""
                                val id = it.snippet?.resourceId?.videoId ?: ""

                                val thumbnailUrl = it.snippet?.thumbnails?.medium?.url ?: ""
                                val durationString = map[id]?.contentDetails?.duration ?: ""
                                val duration = Duration.parse(durationString).inWholeSeconds
                                val viewCount = map[id]?.statistics?.viewCount ?: ""
                                val likeCount = map[id]?.statistics?.likeCount ?: ""
                                val video = Video(
                                    id = id + "_split_" + playlistId,
                                    playlistId = playlistId,
                                    videoPublishedAt = videoPublishedAt,
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
                            videos
                        },
                    )
                }.flow.cachedIn(viewModelScope)
            )
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


    private suspend fun getPlaylistResults(items: List<SearchResponse.Items>): List<Playlist> {
        val list = mutableListOf<Playlist>()
        val playlistIds = items.mapNotNull {
            it.id?.playlistId
        }
        Log.d("TAG", "getPlaylistResults: ${playlistIds.size}")
        val map = getPlaylistVideoCount(playlistIds)
        if (map.size == items.size) {
            items.forEach {
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
            Pager(
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                )
            )
            {
                PlaylistByChannelPagingSource(apiCall = {
                    apiService.getPlaylistByChannelId(
                        channelId,
                        it
                    )
                }) {
                    getPlaylistWithVideoCount(it)
                }
            }.flow.cachedIn(viewModelScope).collectLatest {
                _playlists.value = it
            }
        }
    }

    private suspend fun getPlaylistWithVideoCount(items: List<Items>): List<Playlist> {
        val list = mutableListOf<Playlist>()
        try {
            val playlistIds = items.mapNotNull { it.id }
            val map = getPlaylistVideoCount(playlistIds)
            Log.d(
                "TAG",
                "getPlaylistByChannelId: ${playlistIds}, ${items.size}"
            )
            if (map.size == items.size) {
                items.forEach {
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

            }
        } catch (e: Exception) {
            errorMessage = e.message.toString()

        }
        return list
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
        try {
            playListDao.insertAll(playlist)
        } catch (_: SQLiteConstraintException) {

        }
    }

    suspend fun isPlaylistAlreadyAdded(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            playListDao.getPlaylistById(id)
        }
    }

    fun onQueryChange(value: String) {
        query = value
    }
}

fun getTimeAgo(context: Context, timestampStr: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("GMT+6:00")

    return try {
        val timestamp = dateFormat.parse(timestampStr)?.time ?: 0
        val currentTime = System.currentTimeMillis()

        val timeDifference = currentTime - timestamp

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = months / 12

        when {
            years > 0 -> context.resources.getQuantityString(R.plurals.time_ago_years, years.toInt(), years)
            months > 0 -> context.resources.getQuantityString(R.plurals.time_ago_months, months.toInt(), months)
            weeks > 0 -> context.resources.getQuantityString(R.plurals.time_ago_weeks, weeks.toInt(), weeks)
            days > 0 -> context.resources.getQuantityString(R.plurals.time_ago_days, days.toInt(), days)
            hours > 0 -> context.resources.getQuantityString(R.plurals.time_ago_hours, hours.toInt(), hours)
            minutes > 0 -> context.resources.getQuantityString(R.plurals.time_ago_minutes, minutes.toInt(), minutes)
            else -> context.getString(R.string.time_ago_just_now)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        // Return a fallback string or handle the error accordingly
        "N/A"
    }
}


fun getFormattedNumber(count: Long): String {
    if (count < 1000) return "" + count
    val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
    val decimal = DecimalFormat("#.##").format(count / 1000.0.pow(exp.toDouble()))
    return String.format("%s%c", decimal, "kMGTPE"[exp - 1])
}