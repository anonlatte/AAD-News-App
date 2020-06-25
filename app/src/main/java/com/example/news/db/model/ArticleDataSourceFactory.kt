package com.example.news.db.model

import androidx.paging.DataSource
import com.example.news.repository.NewsRepository
import javax.inject.Inject

class ArticleDataSourceFactory @Inject constructor(private val repository: NewsRepository) :
    DataSource.Factory<Int, Article>() {

    override fun create(): DataSource<Int, Article> {
        return ArticleDataSource(repository)
    }
}