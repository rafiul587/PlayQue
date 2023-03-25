package com.example.youtubeapitesting.models

data class PlaylistWithReminder(
    val id: String,
    val title: String,
    val channelTitle: String,
    val thumbnail: String,
    val itemCount: Int,
    val itemComplete: Int = 0,
    val addedTime: Long,
    val startDate: Long?,
    val endDate: Long?,
    val time: Long?,
    val daysMask: Int?,
)