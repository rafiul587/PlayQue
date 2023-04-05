package com.example.youtubeapitesting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtubeapitesting.data.PlayListDao
import com.example.youtubeapitesting.data.VideoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoDao: VideoDao
) : ViewModel() {
    private var progress = 0L
    fun updateVideo(id: String) = viewModelScope.launch(Dispatchers.IO) {
        videoDao.updateVideo(progress = progress, id = id)
    }

    fun setProgress(value: Long){
        progress = value
    }
}