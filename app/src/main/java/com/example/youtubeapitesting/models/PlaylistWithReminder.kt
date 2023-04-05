package com.example.youtubeapitesting.models

import androidx.room.Embedded

data class PlaylistWithReminder(
    @Embedded
    val list: Playlist,
    @Embedded
    val rem: Reminder?
)