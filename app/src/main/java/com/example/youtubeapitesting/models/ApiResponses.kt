package com.example.youtubeapitesting.models

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


data class ApiResponse(
    @SerializedName("items") var items: ArrayList<Items> = arrayListOf(),
    @SerializedName("nextPageToken") var nextPageToken: String? = null,
    @SerializedName("prevPageToken") var prevPageToken: String? = null,
    @SerializedName("pageInfo" ) var pageInfo : PageInfo?         = PageInfo()
)

data class PageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)

data class Id(
    @SerializedName("channelId") var channelId: String? = null,
    @SerializedName("playlistId") var playlistId: String? = null
)

@Keep
data class Items(
    @SerializedName("id") var id: String? = null,
    @SerializedName("snippet") var snippet: Snippet? = Snippet(),
    @SerializedName("status") var status: Status? = Status(),
    @SerializedName("contentDetails") var contentDetails: ContentDetails? = ContentDetails(),
    @SerializedName("statistics") var statistics: Statistics? = Statistics()
)

/*class DataStateDeserializer : JsonDeserializer<Items> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Items {
        val item: Items = Gson().fromJson(json, Items::class.java)
        val jsonObject = json.asJsonObject
        if (jsonObject.has("id")) {
            val elem = jsonObject["id"]
            if (elem != null && !elem.isJsonNull) {
                if (elem.isJsonPrimitive) {
                    item.id = elem.asString
                } else {
                    item.playlistId = elem.asJsonObject["playlistId"].asString
                    item.channelId = (elem.asJsonObject["channelId"].asString)
                }
                Log.d("TAG", "deserialize: $item")
            }
        }
        return item
    }
}*/
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
    @SerializedName("videoPublishedAt") var videoPublishedAt: String? = null,
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

data class Status (
    @SerializedName("privacyStatus" ) var privacyStatus : String? = null
)