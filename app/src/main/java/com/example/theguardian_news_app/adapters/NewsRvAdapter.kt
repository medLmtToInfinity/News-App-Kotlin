package com.example.theguardian_news_app.adapters

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.models.Article
import okhttp3.internal.http2.Http2Connection
import java.util.concurrent.CompletableFuture.AsynchronousCompletionTask


class NewsRvAdapter: RecyclerView.Adapter<NewsRvAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //    lateinit var articleImage:
//    lateinit var articleTitle: TextView
//    lateinit var articleDateTime: TextView
//    lateinit var articleDescription: TextView
//    lateinit var articleSource: TextView
        val articleImage : ImageView = itemView.findViewById(R.id.articleImage)
        val articleTitle : TextView = itemView.findViewById(R.id.articleTitle)
        val articleSource : TextView = itemView.findViewById(R.id.articleSource)
        val articleDateTime :  TextView = itemView.findViewById(R.id.articleDateTime)
        val articleDescription : TextView = itemView.findViewById(R.id.articleDescription)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        //check if the objects items are the same
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    //determine the difference between two listes on thread (BackList)
    val diff = AsyncListDiffer(this, differCallback)

    private var onItemClickListener : ((Article) -> Unit)? = null
    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: NewsRvAdapter.ArticleViewHolder, position: Int) {
        val article = diff.currentList[position]

        Log.e("Articles issue", article.id.toString())

        if(article == null){
            return;
        }
        holder.itemView.apply {
            Glide.with(this)
                .load(article.urlToImage)
                .placeholder(R.drawable.loading__1_)
                .error(R.drawable.ic_launcher_background)
                .into(holder.articleImage)

            holder.articleSource.text = article.source?.name ?: ""
            holder.articleTitle.text = article.title
            holder.articleDescription.text = article.description
            holder.articleDateTime.text = article.publishedAt


            setOnClickListener {
                article?.let {
                    onItemClickListener?.invoke(it)
                }
            }
        }


    }

    //for external classes
    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder{
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent,  false)
        return ArticleViewHolder(layout)
    }





}