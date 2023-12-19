package com.qubartech.playque.models

import com.google.gson.annotations.SerializedName

class SearchResponse(
    @SerializedName("items") var items: ArrayList<Items> = arrayListOf(),
    @SerializedName("nextPageToken") var nextPageToken: String? = null,
    @SerializedName("prevPageToken") var prevPageToken: String? = null,
) {
    data class Id(
        @SerializedName("channelId") var channelId: String? = null,
        @SerializedName("playlistId") var playlistId: String? = null
    )

    data class Items(
        @SerializedName("id") var id: Id? = Id(),
        @SerializedName("snippet") var snippet: Snippet? = Snippet(),
    )
}

