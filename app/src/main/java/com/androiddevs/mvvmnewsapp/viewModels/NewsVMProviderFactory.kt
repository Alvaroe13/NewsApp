package com.androiddevs.mvvmnewsapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo

/**
 * we created this provider factory since we're gonna be passing the NewsRepo object as param in NewsViewModel
 */
class NewsVMProviderFactory(
    val newsRepo : NewsRepo
): ViewModelProvider.Factory {



    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsFeedViewModel(newsRepo) as T
    }


}