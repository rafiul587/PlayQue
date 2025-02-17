package com.qubartech.playque.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubartech.playque.data.local.PlayListDao
import com.qubartech.playque.data.remote.ApiService
import com.qubartech.playque.models.Playlist
import com.qubartech.playque.models.PlaylistWithReminder
import com.qubartech.playque.models.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val playListDao: PlayListDao
) : ViewModel() {
    private val _playlistInfo =
        MutableStateFlow<List<PlaylistWithReminder>>(emptyList())
    val playlistInfo: Flow<List<PlaylistWithReminder>>
        get() = _playlistInfo
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
                        }?.let { playlist ->
                            playListDao.insertAll(playlist)
                        }
                    }
                } else {
                    errorMessage = response.errorBody().toString()
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    private fun getActivePlaylists() = viewModelScope.launch(Dispatchers.IO) {
        _playlistInfo.emitAll(playListDao.getActivePlaylists())
    }


    fun saveReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.insertReminder(reminder = reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.deleteReminder(reminder = reminder)
    }

    fun updateReminderStatus(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.updateReminderStatus(reminder)
    }

    fun moveToTrash(playlist: Playlist) = viewModelScope.launch(Dispatchers.IO) {
        playListDao.updatePlaylist(playlist = playlist)
    }

    init {
        getActivePlaylists()
    }
}