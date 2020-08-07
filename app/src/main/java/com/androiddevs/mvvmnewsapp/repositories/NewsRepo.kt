package com.androiddevs.mvvmnewsapp.repositories

import com.androiddevs.mvvmnewsapp.api.RetrofitGenerator
import com.androiddevs.mvvmnewsapp.dbLocal.ArticleDatabase

class NewsRepo(
    val dbLocal : ArticleDatabase
) {

    //retrieve breaking news from api
    suspend fun retrieveBreakingNews(countryCode: String, page: Int) =
        RetrofitGenerator.apiConnection.fetchBreakingNews(countryCode, page)
}