package com.example.redditdemoapplication.data

import com.squareup.moshi.*

@JsonClass(generateAdapter = true)
data class SubredditListing(
    @Json(name = "data") val data: SubredditData,
)

data class SubredditData(
    @Json(name = "after") val after: String? = null,
    @Json(name = "before") val before: String? = null,
    @Json(name = "children") val children: List<SubredditInfo>,
)

data class SubredditInfo(
    @Json(name = "kind") val kind: String,
    @Json(name = "data") val data: Subreddit
)

data class Subreddit(
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "display_name_prefixed") val displayNamePrefixed: String? = null,
    @Json(name = "subscribers") val subscribers: Int? = null,
    @Json(name = "name") val name: String,
    @Json(name = "id") val id: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "header_img", ignore = true) val headerImg: String? = null,
    @Json(name = "subreddit") val subreddit: String? = null,
    @Json(name = "subreddit_name_prefixed") val subredditNamePrefixed: String? = null,
    @Json(name = "user_is_subscriber") var userIsSubscriber: Boolean? = null,
    @Json(name = "user_has_favorited") val userHasFavorited: Boolean? = null,
)