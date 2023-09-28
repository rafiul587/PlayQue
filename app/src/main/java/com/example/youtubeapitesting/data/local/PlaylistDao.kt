package com.example.youtubeapitesting.data.local

import androidx.room.*
import com.example.youtubeapitesting.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {
    @Query(
        "SELECT p.*, r.*, COUNT(CASE WHEN v.progress >= v.duration - 1 THEN 1 END) as itemComplete FROM playlist p LEFT JOIN reminder r" +
                " ON p.id = r.playlistId LEFT JOIN video v ON p.id = v.playlistId WHERE p.isTrash = false GROUP BY p.id ORDER BY p.addedTime DESC"
    )
    fun getActivePlaylists(): Flow<List<PlaylistWithReminder>>

    @Query("SELECT * FROM playlist p WHERE p.isTrash = true ORDER BY p.addedTime DESC")
    fun getTrashedPlaylist(): Flow<List<Playlist>>

    @Query("SELECT 1 FROM playlist WHERE id=:id LIMIT 1")
    fun getPlaylistById(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder)

    @Update
    fun updatePlaylist(playlist: Playlist)

    @Delete
    fun delete(playlist: Playlist)
}