package com.qubartech.playque.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys", foreignKeys = [ForeignKey(
    Playlist::class,
    arrayOf("id"), arrayOf("id"), onDelete = ForeignKey.CASCADE
)])
data class RemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val nextKey: String?,
    val prevKey: String?)