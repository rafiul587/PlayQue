package com.example.youtubeapitesting.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("items") var items: ArrayList<Items> = arrayListOf(),
    @SerializedName("nextPageToken") var nextPageToken: String? = null,
    @SerializedName("prevPageToken") var prevPageToken: String? = null,
)

@Keep
data class Items(
    @SerializedName("id") var id: String? = null,
    @SerializedName("snippet") var snippet: Snippet? = Snippet(),
    @SerializedName("contentDetails") var contentDetails: ContentDetails? = ContentDetails(),
    @SerializedName("statistics") var statistics: Statistics? = Statistics()
)
@Keep
data class Snippet(
    @SerializedName("title") var title: String? = null,
    @SerializedName("thumbnails") var thumbnails: Thumbnails? = Thumbnails(),
    @SerializedName("channelTitle") var channelTitle: String? = null,
    @SerializedName("resourceId") var resourceId: ResourceId? = null,
)

data class Thumbnails(
    @SerializedName("medium") var medium: Medium? = Medium(),
    @SerializedName("default") var default: Default? = Default(),
)

data class Standard(
    @SerializedName("url") var url: String? = null,
)

data class Default(
    @SerializedName("url") var url: String? = null,
)

data class Medium(
    @SerializedName("url") var url: String? = null,
)

data class ContentDetails(
    @SerializedName("duration") var duration: String? = null,
    @SerializedName("itemCount") var itemCount: Int? = null
)

@Keep
data class ResourceId(
    @SerializedName("videoId") var videoId: String? = null
)

data class Statistics (
    @SerializedName("viewCount"             ) var viewCount             : String?  = null,
    @SerializedName("subscriberCount"       ) var subscriberCount       : String?  = null,
    @SerializedName("hiddenSubscriberCount" ) var hiddenSubscriberCount : Boolean? = null,
    @SerializedName("videoCount"            ) var videoCount            : String?  = null,
    @SerializedName("likeCount"            ) var likeCount            : String?  = null
)