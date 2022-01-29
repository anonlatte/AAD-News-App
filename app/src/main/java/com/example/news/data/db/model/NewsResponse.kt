package com.example.news.data.db.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)