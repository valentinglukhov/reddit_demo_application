package com.example.redditdemoapplication.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Response

private const val ACCESS_TOKEN = "access_token"
private const val MY_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjg4MTQ4NzQwLjAyOTA1NSwiaWF0IjoxNjg4MDYyMzQwLjAyOTA1NCwianRpIjoiQWNVRV9xbTF1MnNfeWVYS3p1SjVCQlI3SDE2eXp3IiwiY2lkIjoiMGdDSFBkYVlMSHVRdnlyVE5lY0VzZyIsImxpZCI6InQyX3RlMTduNmtiIiwiYWlkIjoidDJfdGUxN242a2IiLCJsY2EiOjE2NjU4NTg2NzkwMDAsInNjcCI6ImVKeEVqVUVPZzBBSVJlX3kxOXlvNldKRzBKSTZZZ0J0dkgwemF0TWRmQjdfUGZEUnR3cHJnaEJsRjlDWnVCUUdvUm4zRFlRYjJTMDcwbzdZcWd1elp2VEhyY2JnV3Z0cGRkMUxTcE9JTWtsY0pZTXRvMDRnX0h0bm02NWh0Y2diRy1laWZsS3JlZGY5Z3RocU9fM0tzcVRtQWNKTEk4MFBQTDhCQUFEX184SEZReGMiLCJyY2lkIjoiQ2pCcE5uTnMzQU82bUItOWpUeFRlNFNzaVJZN0JlWkRQQTBYQml6ZFVubyIsImZsbyI6OH0.Hnzna-7k4U3ZjIavyC6OhvRiPheGfZ6uDkrVVXwysrUX7P45I-aoIBaxHoJTMXyCFv53ZNuzC2tvjr5yLJyvAbTEmHlcLdjT1tK0GiIfnOBEC7hv4OrKfHa8mTcLpMW1q7FRKWEQDIZiSYeiFww_Fa5E19CgaLKEbhLZyl9e_aJcYn4lweZyZAt_XQc0ZqDHn-Yzrng6JS5QzqoXqR9JuDJW4W7izKgbhXZBvczU9seUK6WZZCalV2iHZXXzMjg0y7oXlBJT7WagBuZLCf1srMbYxH3NF4PyoRjrYvpTiTOo9jQ0T0NJVSYhchLFbXBeNI6wFGe4Ym8ojPCOtKSTjQ"
private const val SETTINGS = "settings"

class Repository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(SETTINGS, MODE_PRIVATE)

    suspend fun favorites(username: String): Response<LinkListing> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.favorites(
            username = username,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
    suspend fun userInfo(): Response<User> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.userInfo(
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
    suspend fun vote(id: String, dir: Int): Response<Result> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.vote(
            dir = dir,
            id = id,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
    suspend fun saveUnsaveItems(id: String, action: String): Response<Result> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.saveUnsaveItems(
            action = action,
            id = id,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
    suspend fun getUserAvatars(ids: String): Response<ResponseBody> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.getUserAvatars(
            ids = ids,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
    suspend fun getRedditLinkWithComments(
        subredditName: String, linkId: String, after: String? = null,
    ): Response<List<LinkListing>> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.getRedditLinkWithComments(
            subredditName = subredditName,
            after = after,
            linkId = linkId,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }

    suspend fun subUnsub(
        action: String, sr: String
    ): Response<Result> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.subUnsub(
            action = action, sr = sr,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }

    suspend fun getRedditLinks(
        subredditName: String, after: String? = null
    ): Response<LinkListing> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.getRedditLinks(
            subredditName = subredditName,
            after = after,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }

    suspend fun getRedditWhere(
        where: String, after: String? = null, query: String? = null
    ): Response<SubredditListing> {
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        return RedditDataApi.getInstance.getRedditWhere(
            where = where,
            after = after,
            query = query,
            access_token = "Bearer $MY_ACCESS_TOKEN"
        )
    }
}