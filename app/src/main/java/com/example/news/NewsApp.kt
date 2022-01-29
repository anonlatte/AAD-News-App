package com.example.news

import android.app.Application
import com.example.news.data.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class NewsApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun androidInjector() = dispatchingAndroidInjector

}
