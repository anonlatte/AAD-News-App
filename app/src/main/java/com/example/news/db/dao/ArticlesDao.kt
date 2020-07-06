package com.example.news.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.news.db.model.Article

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleArticles(article: List<Article>): List<Long>

    @Query("SELECT * FROM articles ORDER BY published_at")
    fun getArticles(): DataSource.Factory<Int, Article>

    @Query("SELECT * FROM articles WHERE title=:title")
    fun getArticlesByTitle(title: String): DataSource.Factory<Int, Article>

    @Query("SELECT MAX(indexInResponse) + 1 FROM articles")
    fun getNextIndexInArticles(): Int

    @Delete
    suspend fun deleteArticle(article: Article): Int

}
