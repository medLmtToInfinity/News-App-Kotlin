package com.example.theguardian_news_app.models

import java.io.Serializable

data class Source(
    val id: String ,
    val name: String? = ""
): Serializable