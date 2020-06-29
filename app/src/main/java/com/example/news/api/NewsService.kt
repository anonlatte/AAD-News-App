package com.example.news.api

import com.example.news.BuildConfig
import com.example.news.db.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NewsService {
    @Headers("X-Api-Key: ${BuildConfig.ApiKey}")
    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("page") page: Int = 1,
        @QueryMap parameters: Map<String, String> = mapOf("country" to "us")
    ): NewsResponse
}