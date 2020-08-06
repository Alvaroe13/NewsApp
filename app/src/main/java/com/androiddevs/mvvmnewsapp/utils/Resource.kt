package com.androiddevs.mvvmnewsapp.utils

/**
 * This class will help us define if answer from ser is successful or not and act accordingly
 * (arch best practices)
 */
sealed class Resource<T>(
    val data: T? = null ,
    val message: String? = null
) {

    class Success<T> (data: T) : Resource<T>(data)
    class Error<T> ( data: T? = null , message: String) : Resource<T>(data , message)
    class Loading<T> : Resource<T>()

}