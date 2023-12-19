package com.qubartech.playque.data.remote.sources

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.qubartech.playque.data.local.AppDatabase
import com.qubartech.playque.models.ApiResponse
import com.qubartech.playque.models.Video
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class VideoListRemoteMediator(
    private val apiCall: suspend (String?) -> Response<ApiResponse>,
    private val map: suspend (ApiResponse) -> List<Video>,
    private val playlistId: String,
    private val database: AppDatabase
) : RemoteMediator<Int, Video>() {

    val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Video>
    ): MediatorResult {
        return try {
            Log.d("TAG", "load: $loadType")
            val nextPageKey = when (loadType) {
                LoadType.REFRESH -> {
                    // Refresh data by making an API request
                    null
                    // Start loading from the initial page after refresh
                }

                LoadType.PREPEND -> {
                    // Not applicable in your case, as you are not using prepend
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {

                    Log.d("TAG", "load: ${state.pages}")

                    val remoteKey = remoteKeyDao.remoteKeyByVideoId(playlistId)
                    // Get the last page from the database

                    // You must explicitly check if the page key is null when
                    // appending, since null is only valid for initial load.
                    // If you receive null for APPEND, that means you have
                    // reached the end of pagination and there are no more
                    // items to load.
                    Log.d("TAG", "load3333: $remoteKey")
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    Log.d("TAG", "load4444: $remoteKey")
                    remoteKey.nextKey
                }
            }

            // Make an API request to fetch data for the current page
            Log.d("TAG", "load: $nextPageKey")
            val response = apiCall(nextPageKey)
            Log.d("TAG", "load: ${response.body()!!.items}")

            // Update the database with the new data
            Log.d("TAG", "load: ${map(response.body()!!).toString()}")

            MediatorResult.Success(endOfPaginationReached = response.body()!!.nextPageToken == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}