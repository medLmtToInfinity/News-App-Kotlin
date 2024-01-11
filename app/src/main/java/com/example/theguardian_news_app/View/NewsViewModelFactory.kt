package com.example.theguardian_news_app.View

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.repository.NewsRepository

class NewsViewModelFactory(val app: Application, val newsRepository: NewsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return NewsViewModel(app, newsRepository) as T
    }


}