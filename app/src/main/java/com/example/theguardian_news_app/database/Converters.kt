package com.example.theguardian_news_app.database

import androidx.room.TypeConverter
import com.example.theguardian_news_app.models.Source

class Converters {
    @TypeConverter

    fun fromSource(source: Source): String {
        return source.id
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}