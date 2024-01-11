package com.example.theguardian_news_app.View.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.View.newsActivity
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.adapters.NewsRvAdapter
import com.example.theguardian_news_app.databinding.FragmentHeadlinesBinding
import com.example.theguardian_news_app.models.NewsAPI_res
import com.example.theguardian_news_app.utilities.Constants
import com.example.theguardian_news_app.utilities.Resource


class headlinesFragment : Fragment(R.layout.fragment_headlines) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsRvAdapter: NewsRvAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadlinesError: CardView
    lateinit var binding: FragmentHeadlinesBinding

    private val TAG = "HeadlinesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_headlines, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)



        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError) as CardView

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.error_activity, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as newsActivity).newsViewModel
        setupHeadlinesRecycler()


        newsRvAdapter.setOnItemClickListener { article ->
            if (article != null) {

                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }
                findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment, bundle)
            } else {
                Toast.makeText(activity, "Selected article is null", Toast.LENGTH_SHORT).show()
            }
        }


        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
                when(response){
                    is Resource.Success<*> -> {
                        hideProgressBar()
                        hideErrorMessage()
                        response.data?.let{ NewsAPI_res ->
                            newsRvAdapter.diff.submitList(NewsAPI_res.articles.toList())
                            val totalPages = NewsAPI_res.totalResults / Constants.QUERY_PAGE_SIZE + 2
                            isLastPage= newsViewModel.headlinesPage == totalPages
                            if(isLastPage){
                                binding.recyclerHeadlines.setPadding(0, 0, 0, 0)

                            }
                        }
                    }
                    is Resource.Error<*> -> {
                        hideProgressBar()
                        response.message?.let{message ->
                            Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_LONG).show()
                            showErrorMessage(message)
                        }
                    }
                    is Resource.Loading<*> ->{
                        showProgressBar()
                    }
                }
        })

        retryButton.setOnClickListener {
            newsViewModel.getHeadlines("us")
        }

    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String){
        itemHeadlinesError.visibility = View.VISIBLE
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
                newsViewModel.getHeadlines("ma")
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

        private fun setupHeadlinesRecycler(){
            newsRvAdapter = NewsRvAdapter()
            binding.recyclerHeadlines.apply{
                adapter = newsRvAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@headlinesFragment.scrollLisner)
            }
        }

}



