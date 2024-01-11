package com.example.theguardian_news_app.models

data class NewsAPI_res(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)