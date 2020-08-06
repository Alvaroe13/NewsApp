package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_layout.view.*

class NewsFeedAdapter : RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder>() {


    //This class is the DiffUtil.
    private val differInfoCallback = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url //we chose url to compare old and new item since every url is different
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    private val differAsync = AsyncListDiffer(this, differInfoCallback)


    inner class NewsFeedViewHolder(item: View) : RecyclerView.ViewHolder(item)



    //------------------------------------ recycler override functions----------------------------//


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_search_news, parent, false)
        return NewsFeedViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: NewsFeedViewHolder, position: Int) {
        val article = differAsync.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvSource.text = article.source.name
            tvPublishedAt.text = article.publishedAt

            setOnClickListener{
                onItemClick?.let { it(article) }
            }
        }
    }

    override fun getItemCount(): Int {
        //we get the list from the AsyncListDiffer since we're not gonna feed the adapter through it's constructor
        //we use "currentList" to retrieve the list of items already compared and dispatched
        return differAsync.currentList.size
    }



    //------------------click Listeners------------------------------//

    //with this we wire the click event done in the itemView to the fun to be handle outside the adapter
    private var onItemClick : ((Article) -> Unit) ? = null


    //this one is to handle click listeners from outside the adapter
    fun setOnItemClickListener(listener : (Article) -> Unit){
        onItemClick = listener
    }
}