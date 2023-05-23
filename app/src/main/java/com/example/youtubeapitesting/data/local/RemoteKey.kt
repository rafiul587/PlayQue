package com.example.youtubeapitesting.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(@PrimaryKey(autoGenerate = false) val id: String, val nextKey: String?, val prevKey: String?)