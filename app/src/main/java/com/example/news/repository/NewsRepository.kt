package com.example.news.repository

import com.example.news.db.model.Article

interface NewsRepository {
    fun getTopHeadlines(): Listing<Article>
}
