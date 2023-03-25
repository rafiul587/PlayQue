package com.example.youtubeapitesting.models

import androidx.room.Entity
import androidx.room.PrimaryKey
data class VideoEntity(
    val id: String,
    val playListId: String,
    val duration: Long = 0,
    val progress: Long = 0
)