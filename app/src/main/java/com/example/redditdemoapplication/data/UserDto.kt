package com.example.redditdemoapplication.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "data") val data: User,
)

data class User(
    @Json(name = "subreddit") val subreddit: UserData,
    @Json(name = "total_karma") val totalKarma: Int,
    @Json(name = "name") val name: String
)

data class UserData(
    @Json(name = "display_name") val displayName: String,
    @Json(name = "icon_img") val iconImg: String,
    @Json(name = "name") val name: String,
    @Json(name = "subscribers") val subscribers: String,
    @Json(name = "url") val url: String,
)