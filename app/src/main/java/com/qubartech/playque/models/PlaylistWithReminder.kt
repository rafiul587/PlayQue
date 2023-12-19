package com.qubartech.playque.models

import androidx.room.Embedded

data class PlaylistWithReminder(
    @Embedded
    val list: Playlist,
    @Embedded
    val rem: Reminder?
)