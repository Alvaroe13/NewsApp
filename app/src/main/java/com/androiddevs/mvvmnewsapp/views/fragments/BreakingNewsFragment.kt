package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    val TAG = "BreakingNewsFragment"

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initRecycler()
        subscribeViewModel()



    }

    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvBreakingNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)}
    }

    private fun subscribeViewModel(){
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { apiResponse ->
            when(apiResponse){
                is Resource.Success ->{
                    hideProgressBar()
                    apiResponse.data?.let {finalResponse ->
                        recyclerAdapter.differAsync.submitList(finalResponse.articles)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    apiResponse.message?.let { errorMessage ->
                        Log.e(TAG, "error here!!!")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }
}