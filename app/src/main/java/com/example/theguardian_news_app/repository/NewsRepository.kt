package com.example.theguardian_news_app.repository

import androidx.room.Query
import com.example.theguardian_news_app.api.RetrofitInstance
import com.example.theguardian_news_app.database.Article_DB
import com.example.theguardian_news_app.models.Article

class NewsRepository(val db: Article_DB) {
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchNews(searchQuery, pageNumber)
    suspend fun upset(article: Article) = db.getArticleDao().upset(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}