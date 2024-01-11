package com.example.theguardian_news_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.theguardian_news_app.models.Article

@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class Article_DB: RoomDatabase(){
    abstract fun getArticleDao(): ArticleDAO.ArticleDAO
    companion object{
        @Volatile //garanti that changes made by one thread
        private var instance: Article_DB? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{
                    instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                Article_DB::class.java,
                "article_db.db"
            )
                .fallbackToDestructiveMigration() // Example: handle migrations
                .build()

        }
}