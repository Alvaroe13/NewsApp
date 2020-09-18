package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.adapters.SpinnerAdapter
import com.androiddevs.mvvmnewsapp.models.ResponseApi
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.COUNTRY_CODE
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.utils.Resource
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.flags_layout.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter
    lateinit var spinner : Spinner
    var loadingState = false
    var isLastPage = false
    var scrollingState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        spinner = (activity as MainActivity).spinnerCountries
        initRecycler()
        subscribeViewModel()
        addingFlags()
        openArticle()
        btnRetry()
        handlingSpinnerOptions()
    }

    private fun handlingSpinnerOptions() {

         spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
             override fun onNothingSelected(parent: AdapterView<*>?) {
             }

             override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,id: Long) {
                 val positon = parent?.getItemAtPosition(position).toString()
                 lateinit var  country : String
                 println("DEBUG, country = ${positon} ")
                 when(positon){
                     2131230839.toString() -> country = "us"
                     2131230837.toString() -> country = "se"
                     2131230841.toString() -> country = "ve"
                 }
                 viewModel.breakingNewsResponse = null
                 viewModel.getBreakingNews(country)
             }

         }
    }

    private fun showSpinner() {
        println("DEBUG, showSpinner called!")
        spinner.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        println("DEBUG, hideSpinner called!")
        spinner.visibility = View.INVISIBLE
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
                    successfulResponse(apiResponse)
                }
                is Resource.Error ->{
                    println("BreakingNewsFragment: call error: ${apiResponse.message}")
                    hideProgressBar()
                    apiResponse.message?.let { errorMessage ->
                        errorResponse(errorMessage)
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun successfulResponse(apiResponse : Resource<ResponseApi>) {
        println("BreakingNewsFragment: call successfully")
        btnRetryBreakingNews.visibility = View.INVISIBLE
        rvBreakingNews.visibility = View.VISIBLE
        hideProgressBar()

        apiResponse.data?.let {finalResponse ->
            if (apiResponse.data.articles.size == 0){
                showNotFoundImage()

                recyclerAdapter.differAsync.submitList(finalResponse.articles.toList())
            }else{
                hideNotFoundImage()
                recyclerAdapter.differAsync.submitList(finalResponse.articles.toList())
                val totalPages = finalResponse.totalResults / QUERY_PAGE_SIZE + 2
                isLastPage = viewModel.breakingNewsPage == totalPages
                if (isLastPage){
                    rvBreakingNews.setPadding(0, 0, 0, 0)
                }
            }

        }
    }

    private fun errorResponse( errorMessage : String) {
        println("DEBUG, errorResponse called, error message = $errorMessage")
        Toast.makeText(activity, "Something went wrong, check your internet connection", Toast.LENGTH_LONG).show()
        btnRetryBreakingNews.visibility = View.VISIBLE
        rvBreakingNews.visibility = View.INVISIBLE
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

    private fun showNotFoundImage(){
        ivBreakingNotFound.visibility = View.VISIBLE
    }

    private fun hideNotFoundImage(){
        ivBreakingNotFound.visibility = View.GONE
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


    override fun onStart() {
        super.onStart()
        showSpinner()
    }

    override fun onPause() {
        super.onPause()
        hideSpinner()
    }

    private fun addingFlags(){

        val imageList = intArrayOf (R.drawable.ic_united_states, R.drawable.ic_sweden_flag, R.drawable.ic_venezuela )
        val customerAdapter = SpinnerAdapter(imageList)
        spinner.adapter =  customerAdapter
    }


}