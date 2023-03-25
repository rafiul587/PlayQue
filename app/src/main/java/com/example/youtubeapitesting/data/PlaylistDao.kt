package com.example.youtubeapitesting.data

import androidx.room.*
import com.example.youtubeapitesting.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {
    @Query("SELECT p.*, r.startDate, r.endDate, r.time, r.daysMask, COUNT(CASE WHEN v.duration = v.progress THEN 1 END) as itemComplete FROM playlist p LEFT JOIN reminder r" +
            " ON p.id = r.playlistId LEFT JOIN video v ON p.id = v.playlistId GROUP BY p.id ORDER BY p.addedTime DESC")
    fun getAll(): Flow<List<PlaylistWithReminder>>

    @Query("SELECT * FROM video WHERE playlistId = :id")
    fun getVideos(id: String) : Flow<List<Video>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideo(videos: List<Video>)

    @Update
    fun updateVideo(video: Video)

    @Delete
    fun delete(playlist: Playlist)
}