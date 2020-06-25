package com.example.news.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.db.model.Article

class ArticlesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item_article, parent, false)
) {

    private val titleTextView = itemView.findViewById<TextView>(R.id.title)
    private val contentTextView = itemView.findViewById<TextView>(R.id.contentPreview)
    private val sourceTextView = itemView.findViewById<TextView>(R.id.sourceName)
    private val articleImageView = itemView.findViewById<ImageView>(R.id.articleImage)
    var article: Article? = null

    fun bindTo(article: Article?) {
        this.article = article
        if (article != null) {
            titleTextView.text = article.title
            contentTextView.text = article.content
            sourceTextView.text = article.source.name

            if (article.urlToImage != null) {
                Glide.with(itemView.context).load(article.urlToImage).into(articleImageView)
            }
        }
    }
}
