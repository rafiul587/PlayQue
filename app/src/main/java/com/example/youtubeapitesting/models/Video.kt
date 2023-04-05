package com.example.youtubeapitesting.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Keep
@Entity
data class Video(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val playlistId: String,
    val title: String,
    val thumbnail: String = "",
    val duration: Long = 0L,
    val progress: Long = 0L,
    @Ignore
    val viewCount: String,
    @Ignore
    val likeCount: String
) {
    constructor(
        id: String, playlistId: String, title: String, thumbnail: String = "", duration: Long = 0L,
        progress: Long = 0L
    ) : this(id, playlistId, title, thumbnail, duration, progress, "" , "")

}
