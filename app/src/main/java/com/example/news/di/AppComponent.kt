package com.example.news.di

import android.app.Application
import com.example.news.NewsApp
import com.example.news.di.module.AppModule
import com.example.news.di.module.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, AppModule::class, MainActivityModule::class]
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