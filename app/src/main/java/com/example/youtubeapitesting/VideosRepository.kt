package com.example.youtubeapitesting

import com.example.youtubeapitesting.data.remote.ApiService
import javax.inject.Inject


class VideosRepository @Inject constructor(val apiService: ApiService) {
    fun getVideosFromPlaylist(playListId: String) {

    }

}
