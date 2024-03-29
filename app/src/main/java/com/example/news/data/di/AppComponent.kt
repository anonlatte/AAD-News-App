package com.example.news.data.di

import android.app.Application
import com.example.news.NewsApp
import com.example.news.data.di.module.AppModule
import com.example.news.data.di.module.DatabaseModule
import com.example.news.data.di.module.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {

    fun inject(newsApp: NewsApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}