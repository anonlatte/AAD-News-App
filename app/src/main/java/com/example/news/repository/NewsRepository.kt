package com.example.news.repository

import com.example.news.api.NewsService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val newsService: NewsService) {

    suspend fun getTopHeadlines(
        page: Int = 1,
        parameters: Map<String, String> = mapOf("country" to "us")
    ) = newsService.getTopHeadlines(page, parameters)
}