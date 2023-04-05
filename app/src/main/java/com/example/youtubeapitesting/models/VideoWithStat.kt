package com.example.youtubeapitesting.models

import androidx.room.Entity
import androidx.room.PrimaryKey
data class VideoWithStat(
    val id: String,
    val playlistId: String,
    val title: String,
    val thumbnail: String = "",
    val duration: Long = 0L,
    val viewCount: Long = 0L,
    val likeCount: Long = 0L,
)