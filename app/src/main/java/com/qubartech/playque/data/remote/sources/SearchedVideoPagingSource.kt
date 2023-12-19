package com.qubartech.playque.data.remote.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qubartech.playque.models.ApiResponse
import com.qubartech.playque.models.Video
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