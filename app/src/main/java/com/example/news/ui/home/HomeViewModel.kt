package com.example.news.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.news.api.MainThreadExecutor
import com.example.news.db.model.ArticleDataSourceFactory
import com.example.news.repository.NewsRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(repository: NewsRepository) : ViewModel() {

    private val dataSource = ArticleDataSourceFactory(repository)

    private val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

    private val executor = MainThreadExecutor()

    var list = LivePagedListBuilder(dataSource, config)
        .setFetchExecutor(executor)
        .build()
}