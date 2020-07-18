package com.example.news.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.news.db.model.Article

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun singleInsert(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun multipleInsert(article: List<Article>): List<Long>

    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun get(): DataSource.Factory<Int, Article>

    @Query("SELECT * FROM articles WHERE title=:title")
    fun getByTitle(title: String): DataSource.Factory<Int, Article>

    @Query("SELECT MAX(indexInResponse) + 1 FROM articles")
    fun getNextIndex(): Int

    @Delete
    suspend fun delete(article: Article): Int

}
