package com.example.news.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var source: Source,
    var author: String?,
    var title: String?,
    var description: String?,
    var url: String?,
    @ColumnInfo(name = "url_to_image")
    var urlToImage: String?,
    @ColumnInfo(name = "published_at")
    var publishedAt: String?,
    var content: String?
)