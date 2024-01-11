package com.example.theguardian_news_app.View.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.View.newsActivity
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar


class articleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel
    val args: articleFragmentArgs by navArgs()

    lateinit var binding: FragmentArticleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel  = (activity as newsActivity).newsViewModel
        val article = args.article

        binding.webView.apply{
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        binding.savedNews.setOnClickListener{
            newsViewModel.addToFavourites(article)
            Snackbar.make(view,"Added to favourites", Snackbar.LENGTH_SHORT).show()
        }
    }



}