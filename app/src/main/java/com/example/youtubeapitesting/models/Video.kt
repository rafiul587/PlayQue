package com.example.youtubeapitesting.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Video(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val playlistId: String,
    val title: String,
    val thumbnail: String = "",
    val duration: Long = 0L,
    val progress: Long = 0L,
)
