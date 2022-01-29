package com.example.news.data.di.module

import com.example.news.BuildConfig
import com.example.news.data.api.NewsService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun providesNewsService(): NewsService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsService::class.java)
    }
}