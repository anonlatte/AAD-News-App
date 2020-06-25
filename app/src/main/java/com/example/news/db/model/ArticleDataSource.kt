package com.example.news.db.model

import androidx.paging.PageKeyedDataSource
import com.example.news.repository.NewsRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ArticleDataSource @Inject constructor(private val repository: NewsRepository) :
    PageKeyedDataSource<Int, Article>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Article>
    ) {

        val articles = runBlocking {
            repository.getTopHeadlines()
        }.articles

        callback.onResult(articles, null, 2)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        // TODO Not yet implemented
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        val page = params.key

        val articles = runBlocking {
            repository.getTopHeadlines(page)
        }.articles
        callback.onResult(articles, page + 1)
    }
}