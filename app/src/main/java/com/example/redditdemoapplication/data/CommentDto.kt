package com.example.redditdemoapplication.data

import com.squareup.moshi.*


@JsonClass(generateAdapter = true)
data class CommentListing(
    @Json(name = "data") val data: CommentData? = null,
)

data class CommentData(
    @Json(name = "after") val after: String? = null,
    @Json(name = "before") val before: String? = null,
    @Json(name = "children") val children: List<CommentInfo>,
)

data class CommentInfo(
    @Json(name = "kind") val kind: String,
    @Json(name = "data") val data: Comment? = null
)

data class Comment(
    @Json(name = "score") val score: Int? = null,
    @Json(name = "subreddit_id") val subredditId: String? = null,
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "author") val author: String? = null,
    @Json(name = "author_fullname") val authorFullname: String? = null,
    @Json(name = "likes") val likes: Boolean? = null,
    @Json(name = "saved") val saved: Boolean? = null,
)