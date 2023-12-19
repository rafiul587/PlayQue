package com.qubartech.playque

import com.qubartech.playque.data.remote.ApiService
import javax.inject.Inject


class VideosRepository @Inject constructor(val apiService: ApiService) {
    fun getVideosFromPlaylist(playListId: String) {

    }

}
