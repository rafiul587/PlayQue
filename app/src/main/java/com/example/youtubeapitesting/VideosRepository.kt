package com.example.youtubeapitesting

import javax.inject.Inject


class VideosRepository @Inject constructor(val apiService: ApiService) {
    fun getVideosFromPlaylist(playListId: String) {

    }

}
