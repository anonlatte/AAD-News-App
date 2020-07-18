package com.example.news.api

import androidx.paging.PagedList
import com.example.news.db.model.Article
import com.example.news.db.model.NewsResponse
import com.example.news.repository.network.createStatusLiveData
import com.example.news.util.PagingRequestHelper
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.Executor

class ArticlesBoundaryCallback(
    private val webservice: NewsService,
    private val handleResponse: (NewsResponse) -> Unit,
    private val ioExecutor: Executor
) : PagedList.BoundaryCallback<Article>() {

    private var page: Int = 1

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    companion object {
        const val API_MAX_PAGES: Int = 3
    }

    override fun onZeroItemsLoaded() {
        Timber.d("ZeroItemsLoaded")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { requestCallback ->
            try {
                val response = runBlocking {
                    webservice.getTopHeadlines()
                }
                insertItemsIntoDb(response, requestCallback)
            } catch (throwable: Throwable) {
                requestCallback.recordFailure(throwable)
            }
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Article) {
        Timber.d("ItemAtEndLoaded")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { requestCallback ->
            page++
            if (page < API_MAX_PAGES) {
                try {
                    val response = runBlocking {
                        webservice.getTopHeadlines(page)
                    }
                    insertItemsIntoDb(response, requestCallback)

                } catch (throwable: Throwable) {
                    requestCallback.recordFailure(throwable)
                }
            }
        }
    }


    override fun onItemAtFrontLoaded(itemAtFront: Article) {
        Timber.d("ItemAtFrontLoaded")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.BEFORE) { requestCallback ->
            try {
                val response = runBlocking {
                    webservice.getTopHeadlines()
                }

                insertItemsIntoDb(response, requestCallback)
            } catch (throwable: Throwable) {
                requestCallback.recordFailure(throwable)
            }
        }

    }

    private fun insertItemsIntoDb(
        response: NewsResponse,
        requestCallback: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(response)
            requestCallback.recordSuccess()
        }
    }
}
