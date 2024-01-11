package com.example.theguardian_news_app.View.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.InvalidationTracker
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.View.newsActivity
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.adapters.NewsRvAdapter
import com.example.theguardian_news_app.databinding.FragmentSavedBinding
import com.google.android.material.snackbar.Snackbar


class savedFragment : Fragment(R.layout.fragment_saved) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsRvAdapter: NewsRvAdapter
    lateinit var binding: FragmentSavedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedBinding.bind(view)



        newsViewModel = (activity as newsActivity).newsViewModel
        setupSavedRecycler()


        newsRvAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedFragment_to_articleFragment, bundle)
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsRvAdapter.diff.currentList[position]

                newsViewModel.deleteArticle(article)
                Snackbar.make(view, "Removed from favourites", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.addToFavourites(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerSaved)
        }
        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer { articles ->
                newsRvAdapter.diff.submitList(articles)
        })
    }
    private fun setupSavedRecycler(){
        newsRvAdapter = NewsRvAdapter()
        binding.recyclerSaved.apply{
            adapter = newsRvAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}