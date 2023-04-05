package com.example.youtubeapitesting

import com.example.youtubeapitesting.models.ApiResponse
import com.example.youtubeapitesting.models.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("playlistItems?part=snippet&fields=nextPageToken,items(snippet.resourceId.videoId, snippet.title,snippet.thumbnails.medium.url)")
    suspend fun getVideosFromPlaylist(@Query("playlistId") playlistId: String, @Query("nextPageToken") pageToken : String?): Response<ApiResponse>
    @GET("playlists?part=snippet%2CcontentDetails&fields=items(snippet.title,snippet.thumbnails.medium.url,snippet.channelTitle,contentDetails.itemCount))")
    suspend fun getPlaylist(@Query("id") playlistId: String): Response<ApiResponse>
    @GET("videos?part=contentDetails%2Cstatistics&fields=items(id,contentDetails.duration,statistics.viewCount,statistics.likeCount)")
    suspend fun getVideo(@Query("id") videoIds: List<String>): Response<ApiResponse>
    @GET("search?part=snippet&fields=items(id,snippet.title,snippet.channelTitle,snippet.thumbnails.medium.url)")
    suspend fun search(@Query("q") query: String, @Query("type") type: String): Response<SearchResponse>
    @GET("channels?part=statistics")
    suspend fun getChannelStats(@Query("id") channelId: List<String>): Response<ApiResponse>
    @GET("playlists?part=snippet%2CcontentDetails&fields=items(id,snippet.title,snippet.thumbnails.medium.url,contentDetails.itemCount)")
    suspend fun getPlaylistByChannelId(@Query("channelId") channelId: String): Response<ApiResponse>

    @GET("playlists?part=contentDetails&fields=items(id,contentDetails.itemCount)")
    suspend fun getPlaylistVideoCount(@Query("id") playlistIds: List<String>): Response<ApiResponse>
}