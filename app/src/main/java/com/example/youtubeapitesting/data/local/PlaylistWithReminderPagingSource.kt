package com.example.youtubeapitesting.data.local;

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.youtubeapitesting.models.PlaylistWithReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistWithReminderPagingSource(
    private val playListDao: PlayListDao
) : PagingSource<Int, PlaylistWithReminder>() {
    /*
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaylistWithReminder> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        val offset = (page - 1) * pageSize
        val playlists = withContext(Dispatchers.IO){playListDao.getActivePlaylists(offset, pageSize)}
        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (playlists.isEmpty()) null else page + 1

        return LoadResult.Page(
            data = playlists,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PlaylistWithReminder>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }*/
    override fun getRefreshKey(state: PagingState<Int, PlaylistWithReminder>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaylistWithReminder> {
        TODO("Not yet implemented")
    }
}

