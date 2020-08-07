package com.androiddevs.mvvmnewsapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.ResponseApi
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.COUNTRY_CODE
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsFeedViewModel(
    val newsRepo : NewsRepo
) : ViewModel(){


    val breakingNews: MutableLiveData<Resource<ResponseApi>> = MutableLiveData()
    var breakingNewsPage = 1

    init {
        getBreakingNews(COUNTRY_CODE)
    }

    //lets launch the coroutines here with the viewModelScope
    //(as long as the vm is alive so is the coroutine)
    fun getBreakingNews(countryCode: String ) = viewModelScope.launch {

        breakingNews.postValue(Resource.Loading())
        //fetch info from server
        val response = newsRepo.retrieveBreakingNews(countryCode, breakingNewsPage)
        //once is processed by our function we post it in LiveData
        breakingNews.postValue(processResponse(response))
    }

    /**
     * in this function we process the incoming response from the server using the Resource class
     * (part of best practices)
     */
    private fun processResponse(response : Response<ResponseApi>) : Resource<ResponseApi>{
        if (response.isSuccessful){
            response.body()?.let { responseResult ->
                return Resource.Success(responseResult)
            }
        }
        return Resource.Error(null , response.message())
    }

}