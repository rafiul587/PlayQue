package com.example.youtubeapitesting.data.remote.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.youtubeapitesting.models.ApiResponse
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.Video
import retrofit2.Response

class SearchedVideoPagingSource(
    private val apiCall: suspend (String?) -> Response<ApiResponse>,
    private val map: suspend (ApiResponse) -> List<Video>
) : PagingSource<String, Video>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Video> {
        try {
            val nextPageToken = params.key

            Log.d("TAG", "load: $nextPageToken")
            val response = apiCall(nextPageToken)

            val videos = map(response.body()!!)
            val prevKey = response.body()!!.prevPageToken

            val nextKey = response.body()!!.nextPageToken
            Log.d("TAG", "load2222: $videos")


            return LoadResult.Page(
                data = videos,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return null
    }
}