package com.example.theguardian_news_app.api

import com.example.theguardian_news_app.models.NewsAPI_res
import com.example.theguardian_news_app.utilities.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//API Interface
interface NewsAPI_req {
    @GET("v2/top-headlines") // endpoint
    //function can be paused and resumed in async way
    suspend fun getHeadlines(
        @Query("country")
        countryCode : String = "us",
        @Query("page")
        pageNbr : Int = 1,
        @Query("apiKey")
        apiKey : String = API_KEY
    ): Response<NewsAPI_res>
    //return response
    //search query
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        keyWord: String,
        @Query("page")
        pageNbr : Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ):Response<NewsAPI_res>
}