package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.models.ResponseApi
import com.androiddevs.mvvmnewsapp.utils.ApiUtils.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {


    //fetch info related to the US
    @GET("v2/top-headlines?")
    suspend fun fetchBreakingNews( @Query ("country") countryCode: String ="us",
                                   @Query("page") pageNumber: Int = 1,
                                   @Query("apiKey") apiKey: String = API_KEY ) : Response<ResponseApi>

    //fetch news from all around the world
    @GET("v2/everything?")
    suspend fun fetchAllNews( @Query ("q") search: String ="us",
                                   @Query("page") pageNumber: Int = 1,
                                   @Query("apiKey") apiKey: String = API_KEY ) : Response<ResponseApi>



}