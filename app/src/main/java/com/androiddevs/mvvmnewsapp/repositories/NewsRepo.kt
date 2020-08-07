package com.androiddevs.mvvmnewsapp.repositories

import com.androiddevs.mvvmnewsapp.api.RetrofitGenerator
import com.androiddevs.mvvmnewsapp.dbLocal.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

class NewsRepo(
    val dbLocal : ArticleDatabase
) {

    //----------------------api----------------------------//

    suspend fun retrieveBreakingNews(countryCode: String, page: Int) =
        RetrofitGenerator.apiConnection.fetchBreakingNews(countryCode, page)

    suspend fun searchNews(query: String, pageNumber : Int) =
        RetrofitGenerator.apiConnection.fetchAllNews(query, pageNumber)


    //----------------------- local cache ------------------------------//

    suspend fun saveNewInDb(article: Article) =  dbLocal.getDao().saveUpdate(article)

    fun getAllArticles() = dbLocal.getDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = dbLocal.getDao().deleteArticle(article)
}