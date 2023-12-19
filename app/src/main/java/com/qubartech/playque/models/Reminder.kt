package com.qubartech.playque.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(Playlist::class,
    arrayOf("id"), arrayOf("playlistId"), onDelete = ForeignKey.CASCADE)]
)
data class Reminder(
    @PrimaryKey(autoGenerate = false)
    val playlistId: String,
    val startDate: Long,
    val endDate: Long,
    val time: Long,
    val daysMask: Int,
    val isEnabled: Boolean = false
)