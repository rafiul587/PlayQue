package com.qubartech.playque.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Keep
@Entity(foreignKeys = [ForeignKey(Playlist::class,
    arrayOf("id"), arrayOf("playlistId"), onDelete = ForeignKey.CASCADE)]
)
data class Video(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(index = true)
    val playlistId: String,
    val videoPublishedAt: String,
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
        id: String, playlistId: String, videoPublishedAt: String, title: String, thumbnail: String = "", duration: Long = 0L,
        progress: Long = 0L
    ) : this(id, playlistId, videoPublishedAt, title, thumbnail, duration, progress, "" , "")

}
