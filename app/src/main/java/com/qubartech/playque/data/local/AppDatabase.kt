package com.qubartech.playque.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubartech.playque.models.Playlist
import com.qubartech.playque.models.Reminder
import com.qubartech.playque.models.RemoteKey
import com.qubartech.playque.models.Video

@Database(entities = [Playlist::class, Reminder::class, Video::class, RemoteKey::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlayListDao
    abstract fun videoDao(): VideoDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}