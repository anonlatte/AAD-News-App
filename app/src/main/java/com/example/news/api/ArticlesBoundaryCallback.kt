package com.example.news.api

import androidx.paging.PagedList
import com.example.news.db.model.Article
import com.example.news.db.model.NewsResponse
import com.example.news.repository.network.createStatusLiveData
import com.example.news.util.PagingRequestHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { requestCallback ->
            try {
                GlobalScope.launch {
                    val response = webservice.getTopHeadlines().apply {
                        Timber.d("$totalResults")
                    }
                    insertItemsIntoDb(response, requestCallback)
                }
            } catch (throwable: Throwable) {
                requestCallback.recordFailure(throwable)
            }
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Article) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { requestCallback ->
            try {
                page++
                GlobalScope.launch {
                    val response = webservice.getTopHeadlines(page).apply {
                        Timber.d("$totalResults")
                    }
                    insertItemsIntoDb(response, requestCallback)
                }
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

    override fun onItemAtFrontLoaded(itemAtFront: Article) {}
}
