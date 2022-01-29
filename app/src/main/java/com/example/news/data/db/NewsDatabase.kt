package com.example.news.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.news.data.db.dao.ArticlesDao
import com.example.news.data.db.model.Article

@Database(entities = [Article::class], version = NewsDatabase.VERSION, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    companion object {
        const val VERSION = 1
    }

    abstract fun articlesDao(): ArticlesDao
}