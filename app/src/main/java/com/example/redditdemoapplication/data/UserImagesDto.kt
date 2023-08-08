package com.example.redditdemoapplication.data

import com.squareup.moshi.*


@JsonClass(generateAdapter = true)
data class UserImages(
    @Json(name = "profile_img") val profileImg: String? = null
)
