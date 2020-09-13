package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.models.ResponseApi
import com.androiddevs.mvvmnewsapp.utils.Constants
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.SEARCH_TIME_DELAY
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter
    var loadingState = false
    var isLastPage = false
    var scrollingState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initRecycler()
        subscribeViewModel()
        searchBar()
        openArticle()
        retryBtn()

    }


    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvSearchNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListenerCustom)
        }
    }

    private fun subscribeViewModel(){
        println("Debugging: initViewModel called")
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { apiResponse ->
            when(apiResponse){
                is Resource.Success ->{
                    hideProgressBar()
                    successfulResponse(apiResponse)
                }
                is Resource.Error ->{
                    hideProgressBar()
                    errorResponse(apiResponse)
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }


    private fun successfulResponse(apiResponse: Resource.Success<ResponseApi>) {

        println("SearchNewsFragment: successfully called")
        apiResponse.data?.let {finalResponse ->
            btnRetrySearchNews.visibility = View.INVISIBLE
            rvSearchNews.visibility = View.VISIBLE
            recyclerAdapter.differAsync.submitList(finalResponse.articles.toList())
            val totalPages = finalResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
            isLastPage = viewModel.searchNewsPage == totalPages
            if(isLastPage){
                rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }

    }

    private fun errorResponse(apiResponse: Resource.Error<ResponseApi>) {
        println("SearchNewsFragment: errorResponse called")
        apiResponse.message?.let { errorMessage ->
            println("SearchNewsFragment, errorResponse, error message = $errorMessage")
            Toast.makeText(activity, "Something went wrong, check your internet connection", Toast.LENGTH_LONG).show()
            btnRetrySearchNews.visibility = View.VISIBLE
            rvSearchNews.visibility = View.INVISIBLE
        }
    }

    private fun openArticle() {
        println("SearchNewsFragment openArticle called")
        recyclerAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("newsArticle", it)
                println("SearchNewsFragment, article title: ${it.title}")
            }

            findNavController().navigate(R.id.action_searchNewsFragment2_to_articleFragment2, bundle)

        }
    }


    /** When user types in a word in the EditText we will add a small delay to avoid making a query to
      the server every time the user types in a letter  */
    private fun searchBar() {
        println("SearchNewsFragment, searchBar called")
        var job: Job? = null

        etSearch.addTextChangedListener {textInserted ->

            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                textInserted?.let {
                    if (textInserted.toString().isNotEmpty()){
                        println("SearchNewsFragment text: $textInserted")
                        viewModel.searchNewsResponse = null
                        viewModel.searchNews(textInserted.toString())
                    }else{
                        println("SearchNewsFragment, text null")
                    }
                }
            }
        }

    }

    /** retry query when internet is down */
    private fun retryBtn() {
        btnRetrySearchNews.setOnClickListener {
             val connection = viewModel.checkInternetConnection()
             if (connection){
                 val searchQuery = etSearch.text.toString()
                 if (searchQuery.isNotEmpty()){
                     MainScope().launch {
                         delay(SEARCH_TIME_DELAY)
                         viewModel.searchNews(searchQuery)
                     }
                 }else{
                     Toast.makeText(context,"field empty", Toast.LENGTH_SHORT).show()
                 }
             }
         }
    }


    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        loadingState = false
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        loadingState = true
    }

    /** scroll handling section */
    val scrollListenerCustom = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !loadingState && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && scrollingState
            if(shouldPaginate) {
                viewModel.getBreakingNews(Constants.COUNTRY_CODE)
                scrollingState = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //this check means if we're scrolling
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                scrollingState = true
            }
        }
    }
}