package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    val TAG = "SavedNewsFragment"

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initRecycler()

        recyclerAdapter.setOnItemClickListener {

            var bundle = Bundle().apply {
                putSerializable("newsArticle", it)
            }

            findNavController().navigate(R.id.action_savedNewsFragment2_to_articleFragment2, bundle)
        }

    }


    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvBreakingNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}