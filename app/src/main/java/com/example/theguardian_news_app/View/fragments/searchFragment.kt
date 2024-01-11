package com.example.theguardian_news_app.View.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.InvalidationTracker
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.View.newsActivity
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.adapters.NewsRvAdapter
import com.example.theguardian_news_app.databinding.FragmentSavedBinding
import com.example.theguardian_news_app.databinding.FragmentSearchBinding
import com.example.theguardian_news_app.utilities.Constants
import com.example.theguardian_news_app.utilities.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.theguardian_news_app.utilities.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class searchFragment : Fragment(R.layout.fragment_search) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsRvAdapter: NewsRvAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemSearchError: CardView
    lateinit var binding: FragmentSearchBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)

        itemSearchError = view.findViewById(R.id.itemSearchError)

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.error_activity, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as newsActivity).newsViewModel

        setupSearchRecycler()

        newsRvAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment, bundle)
        }

        var job: Job? = null

        //Handle the search functionality
        binding.searchEdit.addTextChangedListener {
            job?.cancel() // Cancel any previous pending job
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                val searchText = it.toString()
                if (searchText.isNotEmpty()) {
                    newsViewModel.searchNews(searchText)
                }
            }
        }


        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success<*> -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { NewsAPI_res ->
                        newsRvAdapter.diff.submitList(NewsAPI_res.articles.toList())
                        val totalPages = NewsAPI_res.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }
            }
        })



        retryButton.setOnClickListener{
            if(binding.searchEdit.text.toString().isNotEmpty()){
                newsViewModel.searchNews(binding.searchEdit.text.toString())
            }else{
                hideErrorMessage()
            }
        }

    }


    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }


    val scrollLisner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.itemCount
            val totalItemCount = layoutManager.itemCount


            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPagination =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling


            if (shouldPagination) {
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = false
            }
        }


        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


        private fun setupSearchRecycler() {
            newsRvAdapter = NewsRvAdapter()
            binding.recyclerSearch.apply {
                adapter = newsRvAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@searchFragment.scrollLisner)
            }
        }

}





