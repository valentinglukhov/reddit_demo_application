package com.example.redditdemoapplication.data

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://www.reddit.com/"
private const val REDIRECT_URI = "app://open.my.application"
private const val AUTHORIZATION_CODE = "authorization_code"
private const val REFRESH_TOKEN = "refresh_token"

interface RedditAuthApi {


    @POST("api/v1/access_token/")
    suspend fun getAccessToken(
        @Query("grant_type") grant_type: String = AUTHORIZATION_CODE,
        @Query("code") code: String,
        @Query("redirect_uri") redirect_uri: String = REDIRECT_URI,
        @Header("Authorization") credentials: String
    ) : Response<TokenData>

    @POST("api/v1/access_token/")
    suspend fun getRefreshToken(
        @Query("grant_type") grant_type: String = REFRESH_TOKEN,
        @Query("refresh_token") refresh_token: String,
        @Header("Authorization") credentials: String
    ) : Response<TokenData>

    companion object {
        private val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val moshi =
            Moshi.Builder().addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
        private val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val instance: RedditAuthApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(RedditAuthApi::class.java)
        }
    }
}