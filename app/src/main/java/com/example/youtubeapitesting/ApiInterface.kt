package com.example.youtubeapitesting

import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("playlistItems?part=snippet&fields=items(snippet.resourceId.videoId, snippet.title,snippet.thumbnails.standard.url)&maxResults=50&key=YOUR_YOUTUBE_API_KEY")
    suspend fun getVideosFromPlaylist(@Query("playlistId") playlistId: String): Response<JsonObject>
    @GET("playlists?part=snippet%2CcontentDetails&fields=items(snippet.title,snippet.thumbnails.standard.url,snippet.channelTitle,contentDetails.itemCount)&maxResults=50&key=YOUR_YOUTUBE_API_KEY")
    suspend fun getPlaylistInfo(@Query("id") playlistId: String): Response<JsonObject>
    @GET("videos?part=contentDetails&fields=items(id,contentDetails.duration)&maxResults=50&key=YOUR_YOUTUBE_API_KEY")
    suspend fun getVideoInfo(@Query("id") videoIds: List<String>): Response<JsonObject>
}