package com.qubartech.playque.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: String,
    val title: String,
    val thumbnail: String,
    val numbSub: String,
    val numbVideos: String,
): Parcelable