package com.example.news.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.news.api.ArticlesBoundaryCallback
import com.example.news.api.NewsService
import com.example.news.db.dao.ArticlesDao
import com.example.news.db.model.Article
import com.example.news.db.model.NewsResponse
import com.example.news.repository.network.NetworkState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private var newsService: NewsService,
    private var articlesDao: ArticlesDao
) : NewsRepository {

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun insertResultIntoDb(body: NewsResponse?) {
        GlobalScope.launch {
            Timber.d("Insert result into db ${body?.status}:${body?.totalResults}")
            body!!.articles.let { articles ->
                articlesDao.insertMultipleArticles(articles)
            }
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private suspend fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        try {
            val response = newsService.getTopHeadlines().apply {
                Timber.d("$totalResults")
            }
            ioExecutor.execute {
                GlobalScope.launch {
                    articlesDao.insertMultipleArticles(response.articles)
                }

                // since we are in bg thread now, post the result.
                networkState.postValue(NetworkState.LOADED)
            }

        } catch (throwable: Throwable) {
            networkState.value = NetworkState.error(throwable.message)
        }

        return networkState
    }

    override fun getTopHeadlines(): Listing<Article> {

        val boundaryCallback = ArticlesBoundaryCallback(
            webservice = newsService,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            runBlocking {
                refresh()
            }
        }

        var livePagedList: LiveData<PagedList<Article>>? = null

        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()

        var articlesDataSource: DataSource.Factory<Int, Article>? = null
        runBlocking {
            articlesDataSource = articlesDao.getArticles()
        }

        livePagedList = LivePagedListBuilder(articlesDataSource!!, config)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}