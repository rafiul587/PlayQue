package com.qubartech.playque.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qubartech.playque.models.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE id = :videoId")
    fun remoteKeyByVideoId(videoId: String): RemoteKey

    @Query("DELETE FROM remote_keys WHERE id = :videoId")
    fun deleteByVideoId(videoId: String)
}