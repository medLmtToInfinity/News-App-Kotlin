package com.example.theguardian_news_app.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.theguardian_news_app.models.Article

interface ArticleDAO {

    @Dao
    interface ArticleDAO{
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upset(article: Article): Long

        @Query("SELECT * FROM articles")
        fun getAllArticles(): LiveData<List<Article>>

        @Delete
        suspend fun deleteArticle(article: Article)
    }
}