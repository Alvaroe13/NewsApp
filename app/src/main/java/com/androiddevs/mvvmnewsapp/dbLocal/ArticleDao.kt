package com.androiddevs.mvvmnewsapp.dbLocal

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //replace article if already is saved in db instead of creating a duplicate
    suspend fun saveUpdate(article : Article): Long  //return long for the id of row

    @Query("SELECT * FROM articlesTable")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}