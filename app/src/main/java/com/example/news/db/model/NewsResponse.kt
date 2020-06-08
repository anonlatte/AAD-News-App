package com.example.news.db.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)