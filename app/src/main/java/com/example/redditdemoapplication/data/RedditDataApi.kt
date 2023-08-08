package com.example.redditdemoapplication.data

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://oauth.reddit.com/"

interface RedditDataApi {

    @GET("user/{username}/saved")
    suspend fun favorites(
        @Path("username") username: String,
        @Header("Authorization") access_token: String
    ) : Response<LinkListing>

    @GET("api/v1/me")
    suspend fun userInfo(
        @Header("Authorization") access_token: String
    ) : Response<User>

    @POST("api/vote")
    suspend fun vote(
        @Query("dir") dir: Int,
        @Query("id") id: String,
        @Header("Authorization") access_token: String
    ) : Response<Result>

    @POST("api/{action}")
    suspend fun saveUnsaveItems(
        @Path("action") action: String,
        @Query("id") id: String,
        @Header("Authorization") access_token: String
    ) : Response<Result>

    @GET("api/user_data_by_account_ids")
    suspend fun getUserAvatars(
        @Query("ids") ids: String,
        @Header("Authorization") access_token: String
    ) : Response<ResponseBody>

    @GET("r/{subreddit_name}/comments/{linkId}")
    suspend fun getRedditLinkWithComments(
        @Path("subreddit_name") subredditName: String,
        @Path("linkId") linkId: String,
        @Query("after") after: String? = null,
        @Header("Authorization") access_token: String
    ) : Response<List<LinkListing>>

    @GET("r/{subreddit_name}")
    suspend fun getRedditLinks(
        @Path("subreddit_name") subredditName: String,
        @Query("after") after: String? = null,
        @Header("Authorization") access_token: String
    ) : Response<LinkListing>

    @POST("api/subscribe")
    suspend fun subUnsub(
        @Query("action") action: String,
        @Query("sr") sr: String,
        @Header("Authorization") access_token: String
    ) : Response<Result>

    @GET("subreddits/{where}")
    suspend fun getRedditWhere(
        @Path("where") where: String,
        @Query("after") after: String? = null,
        @Query("q") query: String? = null,
        @Header("Authorization") access_token: String
    ) : Response<SubredditListing>

    companion object {
        private val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val moshi =
            Moshi.Builder()
                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
        private val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val getInstance: RedditDataApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(RedditDataApi::class.java)
        }
    }
}