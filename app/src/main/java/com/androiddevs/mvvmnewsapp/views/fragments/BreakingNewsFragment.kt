package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.COUNTRY_CODE
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

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
        openArticle()
        btnRetry()
    }

    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvBreakingNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListenerCustom)
        }
    }

    private fun subscribeViewModel(){
        println("BreakingNewsFragment: initViewModel Breaking news called!")
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { apiResponse ->
            when(apiResponse){
                is Resource.Success ->{
                    println("BreakingNewsFragment: call successfully")
                    btnRetryBreakingNews.visibility = View.INVISIBLE
                    rvBreakingNews.visibility = View.VISIBLE
                    hideProgressBar()
                    apiResponse.data?.let {finalResponse ->
                        recyclerAdapter.differAsync.submitList(finalResponse.articles.toList())
                        val totalPages = finalResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error ->{
                    println("BreakingNewsFragment: call error: ${apiResponse.message}")
                    hideProgressBar()
                    apiResponse.message?.let { errorMessage ->
                        Toast.makeText(activity, "Something went wrong, check your internet connection", Toast.LENGTH_LONG).show()
                        btnRetryBreakingNews.visibility = View.VISIBLE
                        rvBreakingNews.visibility = View.INVISIBLE
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun openArticle() {
        recyclerAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("newsArticle", it)
                println("SearchNewsFragment, article title: ${it.title}")
            }

            findNavController().navigate(R.id.action_breakingNewsFragment2_to_articleFragment2, bundle)
        }
    }

    private fun btnRetry() {

        btnRetryBreakingNews.setOnClickListener {
            val connection = viewModel.checkInternetConnection()
            if (!connection){
                Toast.makeText(context, "Something went wrong, check your internet connection", Toast.LENGTH_LONG).show()
            }else{
                println("BreakingNewsFragment, onViewCreated : there is internet and it's called!")
                viewModel.getBreakingNews(COUNTRY_CODE)
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && scrollingState
            if(shouldPaginate) {
                viewModel.getBreakingNews(COUNTRY_CODE)
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