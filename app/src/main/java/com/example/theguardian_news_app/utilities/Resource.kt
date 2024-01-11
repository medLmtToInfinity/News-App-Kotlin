package com.example.theguardian_news_app.utilities

sealed class Resource<T> ( //Generique type T
    val data: T? = null,
    val message: String? = null
){
    //SubClasses of Resource
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T?=null): Resource<T>(data, message)
    class Loading<T>: Resource<T>()
}