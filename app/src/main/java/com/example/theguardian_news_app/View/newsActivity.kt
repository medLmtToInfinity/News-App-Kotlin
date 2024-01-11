package com.example.theguardian_news_app.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.databinding.ActivityNewsBinding
import com.example.theguardian_news_app.View.fragments.headlinesFragment
import com.example.theguardian_news_app.View.fragments.profileFragment
import com.example.theguardian_news_app.View.fragments.savedFragment
import com.example.theguardian_news_app.View.fragments.searchFragment
import com.example.theguardian_news_app.ViewModel.NewsViewModel
import com.example.theguardian_news_app.database.Article_DB
import com.example.theguardian_news_app.repository.NewsRepository
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class newsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    lateinit var newsViewModel: NewsViewModel

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*
        val bundle = Bundle()
        bundle.putString("email", intent.getStringExtra("email"))
// set Fragmentclass Arguments
// set Fragmentclass Arguments
        val fragobj = profileFragment()
        fragobj.setArguments(bundle)
*/
        val newsRepository = NewsRepository(Article_DB(this))
        val viewModelFactory = NewsViewModelFactory(application, newsRepository)

        newsViewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Find the NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Connect the bottom navigation view with the navController
        bottomNavigationView.setupWithNavController(navController)




    }
}


