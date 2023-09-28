package com.example.youtubeapitesting.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
): Parcelable