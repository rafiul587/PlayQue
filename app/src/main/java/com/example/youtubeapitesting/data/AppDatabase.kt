package com.example.youtubeapitesting.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youtubeapitesting.models.*

@Database(entities = [Playlist::class, Reminder::class, Video::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlayListDao
}