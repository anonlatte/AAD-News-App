package com.example.news.ui.home

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.news.db.model.Article

class ArticlesAdapter :
    PagedListAdapter<Article, ArticlesViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ArticlesViewHolder(parent)

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Article>() {

            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }
}
