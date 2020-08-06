package com.androiddevs.mvvmnewsapp.viewModels

import androidx.lifecycle.ViewModel
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo

class NewsFeedViewModel(
    val newsRepo : NewsRepo
) : ViewModel(){


}