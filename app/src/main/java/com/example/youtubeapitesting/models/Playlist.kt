package com.example.youtubeapitesting.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val channelTitle: String,
    val thumbnail: String,
    val itemCount: Int,
    val itemComplete: Int = 0,
    val isTrash: Boolean = false,
    val addedTime: Long = System.currentTimeMillis()
)