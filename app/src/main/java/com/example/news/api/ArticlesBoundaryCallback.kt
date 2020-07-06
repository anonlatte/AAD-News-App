package com.example.news.api

import androidx.paging.PagedList
import com.example.news.db.model.Article
import com.example.news.db.model.NewsResponse
import com.example.news.repository.network.createStatusLiveData
import com.example.news.util.PagingRequestHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    /**
     * Database returned 0 items. We should query the backend for more items.
     */

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { requestCallback ->
            try {
                var response: NewsResponse? = null
                runBlocking {
                    response = webservice.getTopHeadlines().apply {
                        Timber.d("$totalResults")
                    }
                }
                GlobalScope.launch {
                    response?.let { insertItemsIntoDb(it, requestCallback) }
                }
            } catch (throwable: Throwable) {
                requestCallback.recordFailure(throwable)
            }
        }
    }

    /**
     * User reached to the end of the list.
     */

    override fun onItemAtEndLoaded(itemAtEnd: Article) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { requestCallback ->
            try {
                page++
                var response: NewsResponse? = null
                runBlocking {
                    response = webservice.getTopHeadlines(page).apply {
                        Timber.d("$totalResults")
                    }
                }
                GlobalScope.launch {
                    response?.let { insertItemsIntoDb(it, requestCallback) }
                }
            } catch (throwable: Throwable) {
                requestCallback.recordFailure(throwable)
            }
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
        response: NewsResponse,
        requestCallback: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(response)
            requestCallback.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Article) {}
}
