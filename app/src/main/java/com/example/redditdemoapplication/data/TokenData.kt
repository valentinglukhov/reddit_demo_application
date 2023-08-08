package com.example.redditdemoapplication.data

import com.squareup.moshi.*


@JsonClass(generateAdapter = true)
data class TokenData(
    @Json(name = "access_token") val access_token: String,
    @Json(name = "token_type") val token_type: String,
    @Json(name = "expires_in") val expires_in: Long,
    @Json(name = "scope") val scope: String,
    @Json(name = "refresh_token") val refresh_token: String
)