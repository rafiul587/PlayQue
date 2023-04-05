package com.example.youtubeapitesting

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.example.youtubeapitesting.models.ApiResponse
import com.example.youtubeapitesting.models.Items
import com.example.youtubeapitesting.models.SearchResponse
import com.example.youtubeapitesting.models.Video
import com.example.youtubeapitesting.navigation.Screens
import kotlin.time.Duration

class VideoListPagingSource(
    private val apiService: ApiService,
    private val playlistId: String,
) : PagingSource<String, Items>() {

    private val TAG = "YoutubePagingSource"

    private val tokens = mutableListOf<String>()
    var token: String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Items> {
        return try {


            val response = apiService.getVideosFromPlaylist(playlistId = playlistId, token)
/*            Log.d("TAG", "C: $tokens, $token}")
            token = response.body()!!.nextPageToken
            val videoIds = response.body()!!.items.map {
                it.snippet?.resourceId?.videoId ?: ""
            }
            val map = getVideoInfo(videoIds)
            val nextPageToken = response.body()?.nextPageToken
            val videos = response.body()?.items?.map {
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
                    viewCount = if (viewCount.isEmpty()) "0" else getFormattedNumber(viewCount.toLong()),
                    likeCount = if (likeCount.isEmpty()) "0" else getFormattedNumber(likeCount.toLong())
                )
                Log.d("TAG", "getVideosFromPlaylist: $")
                video
            } ?: listOf()*/
            LoadResult.Page(
                data = response.body()!!.items,
                prevKey = response.body()?.prevPageToken, //Only page forward
                nextKey = response.body()?.nextPageToken
            )

        } catch (e: Exception) {
            val em = e.message
            Log.e(TAG, "load: $e")
            LoadResult.Error(e)
        }
    }

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
            //errorMessage = e.message.toString()
        }
        return map
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<String, Items>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}