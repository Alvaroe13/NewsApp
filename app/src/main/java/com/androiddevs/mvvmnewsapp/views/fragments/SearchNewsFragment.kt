package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
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

    val TAG = "BreakingNewsFragment"

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initRecycler()
        subscribeViewModel()
        searchBar()

        recyclerAdapter.setOnItemClickListener {

            var bundle = Bundle().apply {
                putSerializable("newsArticle", it)
            }

            findNavController().navigate(R.id.action_searchNewsFragment2_to_articleFragment2, bundle)

        }
    }


    /* When user types in a word in the EditText we will add a small delay to avoid making a query to
      the server every time the user types in a letter  */
    private fun searchBar() {
        var job: Job? = null

        etSearch.addTextChangedListener {textInserted ->

            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                textInserted?.let {
                    if (textInserted.toString().isNotEmpty()){
                        viewModel.searchNews(textInserted.toString())
                    }
                }
            }
        }

    }


    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvSearchNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun subscribeViewModel(){
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { apiResponse ->
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