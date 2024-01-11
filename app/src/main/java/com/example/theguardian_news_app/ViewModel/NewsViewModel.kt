package com.example.theguardian_news_app.ViewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.theguardian_news_app.models.Article
import com.example.theguardian_news_app.models.NewsAPI_res
import com.example.theguardian_news_app.repository.NewsRepository
import com.example.theguardian_news_app.utilities.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query
import java.io.IOException
import java.net.NetworkInterface
import java.sql.Connection
import java.util.Locale.IsoCountryCode

class NewsViewModel(app: Application, val newsRepo: NewsRepository): AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsAPI_res>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsAPI_res? = null

    val searchNews: MutableLiveData<Resource<NewsAPI_res>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsAPI_res: NewsAPI_res? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init{
        getHeadlines("us")
    }
    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }
    private fun handleHeadlinesResponse(response: Response<NewsAPI_res>): Resource<NewsAPI_res>{
        if( response.isSuccessful){
            response.body()?.let { resultResponse ->
                headlinesPage++
                if(headlinesResponse == null){
                    headlinesResponse = resultResponse
                }else{
                    val oldArticles = headlinesResponse?.articles
                    val newsArticles = resultResponse.articles
                    oldArticles?.addAll(newsArticles)
                }
                return Resource.Success(headlinesResponse ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsAPI_res>) : Resource<NewsAPI_res>{
        if( response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchNewsAPI_res == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsAPI_res = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsAPI_res?.articles
                    val newsArticles = resultResponse.articles
                    oldArticles?.addAll(newsArticles)
                }
                return Resource.Success(searchNewsAPI_res ?: resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepo.upset(article)
    }

    fun getFavouriteNews() = newsRepo.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepo.deleteArticle(article)
    }

    fun internetConnection(context: Context): Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run{
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }

    }
    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepo.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))
            }else{
                headlines.postValue(Resource.Error("No internet connection"))
            }

        }catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepo.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))

            }else{
                searchNews.postValue(Resource.Error("No internet"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("No signal"))
            }
        }
    }





}