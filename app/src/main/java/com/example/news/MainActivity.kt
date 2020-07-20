package com.example.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.news.util.TAG_NEWS_WORK
import com.example.news.workers.NewsCheckWorker
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    private val newsCheckWorkRequest: WorkRequest =
        PeriodicWorkRequestBuilder<NewsCheckWorker>(15, TimeUnit.MINUTES)
            .addTag(TAG_NEWS_WORK)
            .build()

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun androidInjector() = dispatchingAndroidInjector

    override fun onPause() {
        super.onPause()
        WorkManager
            .getInstance(applicationContext)
            .enqueue(newsCheckWorkRequest)
    }
}