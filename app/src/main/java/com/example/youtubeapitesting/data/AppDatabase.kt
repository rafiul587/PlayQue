package com.example.youtubeapitesting.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.youtubeapitesting.models.Playlist
import com.example.youtubeapitesting.models.Reminder
import com.example.youtubeapitesting.models.Video

@Database(entities = [Playlist::class, Reminder::class, Video::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlayListDao
    abstract fun videoDao(): VideoDao
}