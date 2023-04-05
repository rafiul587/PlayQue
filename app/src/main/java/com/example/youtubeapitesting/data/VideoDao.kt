package com.example.youtubeapitesting.data

import androidx.room.*
import com.example.youtubeapitesting.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM video WHERE playlistId = :id")
    fun getVideos(id: String): Flow<List<Video>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideo(videos: List<Video>)

    @Update
    fun updateVideo(video: Video)

    @Query("UPDATE video SET progress = :progress WHERE id = :id")
    fun updateVideo(progress: Long, id: String)
}