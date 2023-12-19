package com.qubartech.playque.ui.screens.trash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubartech.playque.data.remote.ApiService
import com.qubartech.playque.data.local.PlayListDao
import com.qubartech.playque.models.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val apiService: ApiService,
    private val playListDao: PlayListDao
) : ViewModel() {
    private val _trashedPlaylists = MutableStateFlow<List<Playlist>>(listOf())
    val trashedPlaylists: StateFlow<List<Playlist>>
        get() = _trashedPlaylists
    var errorMessage: String by mutableStateOf("")

    fun addNewPlaylist(playListId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getPlaylist(playListId)
                if (response.isSuccessful) {
                    response.body()?.items?.forEach {
                        val title =
                            it.snippet?.title ?: ""
                        val channelTitle = it.snippet?.channelTitle ?: ""
                        val itemCount = it.contentDetails?.itemCount ?: 0
                        val thumbnailUrl = it.snippet?.thumbnails?.medium?.url
                        thumbnailUrl?.let { url ->
                            Playlist(
                                id = playListId,
                                title = title,
                                channelTitle = channelTitle,
                                thumbnail = url,
                                itemCount = itemCount
                            )
                        }?.let { playlist -> playListDao.insertAll(playlist) }
                    }
                } else {
                    errorMessage = response.errorBody().toString()
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    private fun getTrashedPlaylists() = viewModelScope.launch(Dispatchers.IO) {
        playListDao.getTrashedPlaylist()
            .collectLatest {
                it.forEach { Log.d("TAG", "getPlaylists: $it") }
                _trashedPlaylists.value = it
            }
    }

    fun deletePlaylistPermanently(playlist: Playlist) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.delete(playlist = playlist)
    }

    fun restoreFromTrash(playlist: Playlist) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.updatePlaylist(playlist = playlist)
    }

    init {
        getTrashedPlaylists()
    }
}