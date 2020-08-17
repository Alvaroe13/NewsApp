package com.androiddevs.mvvmnewsapp.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo

/**
 * Needed a ProviderFactory class to pass params to viewModel
 */
class NewsVMProviderFactory(
    val app : Application,
    val newsRepo : NewsRepo
): ViewModelProvider.Factory {



    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsFeedViewModel( app, newsRepo) as T
    }


}