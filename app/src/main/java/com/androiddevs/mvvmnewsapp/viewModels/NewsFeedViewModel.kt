package com.androiddevs.mvvmnewsapp.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.ResponseApi
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo
import com.androiddevs.mvvmnewsapp.utils.AppsContext
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.COUNTRY_CODE
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsFeedViewModel(
    appContext : Application, // needed to check internet connection
    val newsRepo : NewsRepo
) : AndroidViewModel(appContext){



    val breakingNews: MutableLiveData<Resource<ResponseApi>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse : ResponseApi? = null

    val searchNews: MutableLiveData<Resource<ResponseApi>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse : ResponseApi? = null

    init {
        getBreakingNews(COUNTRY_CODE)
    }

    //---------------------coroutines launch section ---------------------------------//

    //lets launch the coroutines here with the viewModelScope
    //(as long as the vm is alive so is the coroutine)
    fun getBreakingNews(countryCode: String ) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews( query : String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }


    /**
     * this method makes the actual call to server
     */
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(checkInternetConnection()) {
                //fetch info from server
                val response = newsRepo.retrieveBreakingNews(countryCode, breakingNewsPage)
                //once is processed by our function we post it in LiveData
                breakingNews.postValue(processBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error(null, "No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error(null,"Network Failure"))
                else -> breakingNews.postValue(Resource.Error(null,"Conversion Error"))
            }
        }
    }


    /**
     * this method makes the actual call to server
     */
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(checkInternetConnection()) {
                //here we make the call
                val searchNewsResponse = newsRepo.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(processSearchNewsResponse(searchNewsResponse))
            } else {
                searchNews.postValue(Resource.Error(null, "No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error(null, "Network Failure"))
                else -> searchNews.postValue(Resource.Error(null, "Conversion Error"))
            }
        }
    }


    /**
     * in this function we process the incoming response from the server using the Resource class
     * (part of best practices) when getting the breaking news
     */
    private fun processBreakingNewsResponse(response : Response<ResponseApi>) : Resource<ResponseApi>{
        if (response.isSuccessful){
            response.body()?.let { responseResult ->
                //responseResult is the response coming from the server
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = responseResult
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = responseResult.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: responseResult)
            }
        }
        return Resource.Error(null , response.message())
    }

    /**
     * in this function we process the incoming response from the server using the Resource class
     * (part of best practices) when searching for news
     */
    private fun processSearchNewsResponse(response : Response<ResponseApi>) : Resource<ResponseApi>{
        if (response.isSuccessful){
            response.body()?.let { responseResult ->
                //responseResult is the response coming from the server
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = responseResult
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = responseResult.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: responseResult)
            }
        }
        return Resource.Error(null , response.message())
    }

    /**
     * this method will check if device has internet connection when requesting data from server
     */
    private fun checkInternetConnection(): Boolean {
        println("Debugging: called!!")
        val connectivityManager = getApplication<AppsContext>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else { //if device's os version is less than 23 api level
            println("Debugging: called")
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    //------------------------------- local cache ------------------------------------//

    // here launch coroutine
    fun saveArticleInCache(article : Article) = viewModelScope.launch {
        newsRepo.saveNewInDb(article)
    }

    fun getSavedArticles() = newsRepo.getAllArticles()

    fun deleteArticle(article : Article) = viewModelScope.launch {
        newsRepo.deleteArticle(article)
    }


}