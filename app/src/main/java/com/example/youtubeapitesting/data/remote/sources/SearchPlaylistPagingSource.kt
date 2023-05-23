package com.example.youtubeapitesting.data.remote.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.SearchResponse
import com.example.youtubeapitesting.models.SearchResponse.Items
import retrofit2.Response

class SearchPlaylistPagingSource(
    private val apiCall: suspend (String?) -> Response<SearchResponse>,
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