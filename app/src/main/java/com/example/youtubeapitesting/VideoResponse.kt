package com.example.youtubeapitesting

import com.google.gson.annotations.SerializedName

data class VideoResponse (

    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("items"         ) var items         : ArrayList<Items> = arrayListOf(),
    @SerializedName("pageInfo"      ) var pageInfo      : PageInfo?        = PageInfo()

)

data class Snippet (

    @SerializedName("publishedAt"            ) var publishedAt            : String?     = null,
    @SerializedName("channelId"              ) var channelId              : String?     = null,
    @SerializedName("title"                  ) var title                  : String?     = null,
    @SerializedName("description"            ) var description            : String?     = null,
    @SerializedName("thumbnails"             ) var thumbnails             : Thumbnails? = Thumbnails(),
    @SerializedName("channelTitle"           ) var channelTitle           : String?     = null,
    @SerializedName("playlistId"             ) var playlistId             : String?     = null,
    @SerializedName("position"               ) var position               : Int?        = null,
    @SerializedName("resourceId"             ) var resourceId             : ResourceId? = ResourceId(),
    @SerializedName("videoOwnerChannelTitle" ) var videoOwnerChannelTitle : String?     = null,
    @SerializedName("videoOwnerChannelId"    ) var videoOwnerChannelId    : String?     = null

)

data class Items (

    @SerializedName("kind"           ) var kind           : String?         = null,
    @SerializedName("etag"           ) var etag           : String?         = null,
    @SerializedName("id"             ) var id             : String?         = null,
    @SerializedName("snippet"        ) var snippet        : Snippet?        = Snippet(),
    @SerializedName("contentDetails" ) var contentDetails : ContentDetails? = ContentDetails()

)

data class PageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)

data class Thumbnails (

    @SerializedName("default"  ) var default  : Default?  = Default(),
    @SerializedName("medium"   ) var medium   : Medium?   = Medium(),
    @SerializedName("high"     ) var high     : High?     = High(),
    @SerializedName("standard" ) var standard : Standard? = Standard(),
    @SerializedName("maxres"   ) var maxres   : Maxres?   = Maxres()

)

data class Default (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)


data class Medium (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class High (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)


data class Standard (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class Maxres (

    @SerializedName("url"    ) var url    : String? = null,
    @SerializedName("width"  ) var width  : Int?    = null,
    @SerializedName("height" ) var height : Int?    = null

)

data class ResourceId (

    @SerializedName("kind"    ) var kind    : String? = null,
    @SerializedName("videoId" ) var videoId : String? = null

)

data class ContentDetails (

    @SerializedName("videoId"          ) var videoId          : String? = null,
    @SerializedName("videoPublishedAt" ) var videoPublishedAt : String? = null

)
