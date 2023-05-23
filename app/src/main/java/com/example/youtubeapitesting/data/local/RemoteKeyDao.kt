package com.example.youtubeapitesting.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE id = :videoId")
    suspend fun remoteKeyByVideoId(videoId: String): RemoteKey

    @Query("DELETE FROM remote_keys WHERE id = :videoId")
    suspend fun deleteByVideoId(videoId: String)
}