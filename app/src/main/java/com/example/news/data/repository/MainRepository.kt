package com.example.news.data.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.news.data.api.ArticlesBoundaryCallback
import com.example.news.data.api.NewsService
import com.example.news.data.db.NewsDatabase
import com.example.news.data.db.dao.ArticlesDao
import com.example.news.data.db.model.Article
import com.example.news.data.db.model.NewsResponse
import com.example.news.data.repository.network.NetworkState
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private var newsService: NewsService,
    private var database: NewsDatabase,
    private var articlesDao: ArticlesDao
) : NewsRepository {

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun insertResultIntoDb(body: NewsResponse?) {
        var newArticlesCounter = 0
        body!!.articles.let { articles ->
            database.runInTransaction {
                val start = articlesDao.getNextIndex()
                val items = articles.mapIndexed { index, child ->
                    child.indexInResponse = start + index
                    child
                }
                // Count positive indexes
                newArticlesCounter =
                    runBlocking { articlesDao.multipleInsert(items).count { it > 0 } }
            }
        }
        Timber.tag("InsertToDb").d(
            String.format(
                "DB insert status:${body.status}" +
                        ":(retrieved ${body.articles.size} articles)" +
                        ":(new elements:${newArticlesCounter}))"
            )
        )
    }

    @MainThread
    private suspend fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        ioExecutor.execute {
            runBlocking {
                try {
                    val response =
                        newsService.getTopHeadlines().apply {
                            Timber.d("Got $totalResults results")
                        }

                    val newArticlesCounter: Int =
                        articlesDao.multipleInsert(response.articles).count { it > 0 }

                    Timber.tag("Refresh:InsertToDb").d(
                        String.format(
                            "DB insert ${response.articles.size} articles" +
                                    ":(new ${newArticlesCounter}))"
                        )
                    )
                } catch (throwable: Throwable) {
                    networkState.postValue(NetworkState.error(throwable.message))
                }
                networkState.postValue(NetworkState.LOADED)
            }

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

        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()

        val articlesDataSource: DataSource.Factory<Int, Article> = articlesDao.get()

        val livePagedList = LivePagedListBuilder(articlesDataSource, config)
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