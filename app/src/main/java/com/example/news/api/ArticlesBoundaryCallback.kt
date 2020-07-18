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

    override fun onZeroItemsLoaded() {
        Timber.d("ZeroItemsLoaded")
        getNewItems(PagingRequestHelper.RequestType.INITIAL, false)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Article) {
        Timber.d("ItemAtEndLoaded")
        getNewItems(PagingRequestHelper.RequestType.AFTER, true)
    }

    override fun onItemAtFrontLoaded(itemAtFront: Article) {}

    private fun getNewItems(
        requestType: PagingRequestHelper.RequestType,
        isNewPageRequired: Boolean
    ) {
        helper.runIfNotRunning(requestType) { requestCallback ->
            runCatching {
                runBlocking {
                    if (isNewPageRequired) {
                        page++
                        webservice.getTopHeadlines(page)
                    } else {
                        webservice.getTopHeadlines()
                    }
                }
            }.onSuccess { response ->
                insertItemsIntoDb(response, requestCallback)
            }.onFailure { throwable ->
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
