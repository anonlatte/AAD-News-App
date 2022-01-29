package com.example.news.data.db.model

import androidx.room.*

@Entity(tableName = "articles", indices = [Index(value = ["url"], unique = true)])
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var author: String?,
    var title: String?,
    var description: String?,
    var url: String?,
    @ColumnInfo(name = "url_to_image")
    var urlToImage: String?,
    @ColumnInfo(name = "published_at")
    var publishedAt: String?,
    var content: String?
) {
    // FIXME cached article doesn't have source
    @Ignore
    var source: Source? = null

    var indexInResponse: Int = -1
}