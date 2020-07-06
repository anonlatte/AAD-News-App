package com.example.news.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
    private val circularProgressDrawable = CircularProgressDrawable(itemView.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }

    fun bindTo(article: Article?) {
        this.article = article
        article?.run {
            titleTextView.text = title
            contentTextView.text = content
            sourceTextView.text = source?.name

            urlToImage?.let {
                Glide.with(itemView.context)
                    .load(urlToImage)
                    .placeholder(circularProgressDrawable)
                    .into(articleImageView)

            }
        }

    }
}
