package com.qubartech.playque.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.qubartech.playque.models.Video

@Dao
interface VideoDao {

    @Query("SELECT * FROM video WHERE playlistId = :id")
    fun getVideos(id: String): PagingSource<Int, Video>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideo(videos: List<Video>)

    @Update
    fun updateVideo(video: Video)

    @Query("UPDATE video SET progress = :progress WHERE id = :id")
    fun updateVideo(progress: Long, id: String)
}