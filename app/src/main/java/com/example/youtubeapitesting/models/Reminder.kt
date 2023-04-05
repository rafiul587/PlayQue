package com.example.youtubeapitesting.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = false)
    val playlistId: String,
    val startDate: Long,
    val endDate: Long,
    val time: Long,
    val daysMask: Int,
    val isEnabled: Boolean = false
)