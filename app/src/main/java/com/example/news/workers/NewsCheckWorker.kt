package com.example.news.workers

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.news.data.api.NewsService
import com.example.news.data.db.NewsDatabase
import com.example.news.data.db.dao.ArticlesDao
import com.example.news.data.db.model.Article
import com.example.news.data.db.model.NewsResponse
import com.example.news.data.di.module.AppModule
import com.example.news.data.di.module.DatabaseModule
import com.example.news.util.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewsCheckWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private var newsService: NewsService = AppModule().providesNewsService()

    private var database: NewsDatabase = DatabaseModule().providesDatabase(context as Application)

    private var articlesDao: ArticlesDao = database.articlesDao()

    private val appContext = applicationContext

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val newsResponse = withContext(Dispatchers.Default) {
            getData()
        }

        val articlesCounter =
            withContext(Dispatchers.Default) {
                updateData(newsResponse)
            }

        Timber.d("Got $articlesCounter news")

        val firstArticle = newsResponse.articles.first()
        val title = firstArticle.title
        if (articlesCounter > 0) {
            makeStatusNotification(title, "Tap to read another $articlesCounter news", appContext)
        } else {
            val notificationMessage = getCorrectArticleContent(firstArticle)
            makeStatusNotification(title, notificationMessage, appContext)
        }

        Result.success()
    }

    private fun getCorrectArticleContent(article: Article): String? {
        return with(article) {
            if (!description.isNullOrEmpty()) {
                description
            } else if (!content.isNullOrEmpty()) {
                content
            } else {
                null
            }
        }
    }

    private suspend fun getData(): NewsResponse {
        return newsService.getTopHeadlines()
    }

    private suspend fun updateData(body: NewsResponse?): Int {
        var newArticlesCounter = 0
        body!!.articles.let { articles ->
            database.runInTransaction {
                val start = articlesDao.getNextIndex()
                val items = articles.mapIndexed { index, child ->
                    child.indexInResponse = start + index
                    child
                }
                // Count positive indexes
                newArticlesCounter = runBlocking {
                    articlesDao.multipleInsert(items).count { it > 0 }
                }
            }
        }
        return newArticlesCounter
    }

}