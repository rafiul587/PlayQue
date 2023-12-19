package com.qubartech.playque.data.remote.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qubartech.playque.models.ApiResponse
import com.qubartech.playque.models.Items
import com.qubartech.playque.models.Playlist
import retrofit2.Response

class PlaylistByChannelPagingSource(
    private val apiCall: suspend (String?) -> Response<ApiResponse>,
    private val map: suspend (List<Items>) -> List<Playlist>
) : PagingSource<String, Playlist>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Playlist> {
        try {
            val nextPageToken = params.key

            Log.d("TAG", "load: $nextPageToken")
            val response = apiCall(nextPageToken)

            val videos = map(response.body()!!.items)
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

    override fun getRefreshKey(state: PagingState<String, Playlist>): String? {
        return null
    }
}