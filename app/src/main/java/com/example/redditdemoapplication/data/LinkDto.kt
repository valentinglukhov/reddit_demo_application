package com.example.redditdemoapplication.data

import com.squareup.moshi.*


@JsonClass(generateAdapter = true)
data class LinkListing(
    @Json(name = "data") val data: LinkData,
)

data class LinkData(
    @Json(name = "after") val after: String? = null,
    @Json(name = "before") val before: String? = null,
    @Json(name = "children") val children: List<LinkInfo>,
)

data class LinkInfo(
    @Json(name = "kind") val kind: String,
    @Json(name = "data") val data: Link
)

@JsonClass(generateAdapter = true)
data class Link(
    @Json(name = "title") val title: String? = null,
    @Json(name = "score") val score: Int? = null,
    @Json(name = "subreddit_id") val subredditId: String? = null,
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "subreddit") val subreddit: String? = null,
    @Json(name = "num_comments") val numComments: Int? = null,
    @Json(name = "author") val author: String? = null,
    @Json(name = "thumbnail") val thumbnail: String? = null,
    @Json(name = "selftext") val selftext: String? = null,
    @Json(name = "author_fullname") val authorFullname: String? = null,
    @Json(name = "likes") val likes: Boolean? = null,
    @Json(name = "saved") val saved: Boolean? = null,
    @Json(name = "body") val body: String? = null,
    @Json(name = "created_utc") val createdUtc: Double? = null,
    @Json(name = "permalink") val permalink: String? = null,
)