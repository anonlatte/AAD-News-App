package com.example.news.data.repository

import com.example.news.data.db.model.Article

interface NewsRepository {
    fun getTopHeadlines(): Listing<Article>
}
