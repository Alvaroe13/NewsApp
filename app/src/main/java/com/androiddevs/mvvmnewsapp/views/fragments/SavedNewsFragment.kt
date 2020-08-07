package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsFeedAdapter
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    val TAG = "SavedNewsFragment"

    lateinit var viewModel : NewsFeedViewModel
    lateinit var recyclerAdapter : NewsFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        initRecycler()
        showArticlesSaved()
        navigateToArticleFragment()



        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN ,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true //dont do anything with this
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val positionItem = viewHolder.adapterPosition
                val article = recyclerAdapter.differAsync.currentList[positionItem]
                viewModel.deleteArticle(article)


                Snackbar.make(view, "article deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){ viewModel.saveArticleInCache(article)  }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }


    }


    private fun initRecycler() {
        recyclerAdapter = NewsFeedAdapter()
        rvSavedNews.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun showArticlesSaved() {
        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer { articleList ->
            //feed adapter with the list of articles
            recyclerAdapter.differAsync.submitList(articleList)

        })
    }


    private fun navigateToArticleFragment() {
        recyclerAdapter.setOnItemClickListener {

            var bundle = Bundle().apply {
                putSerializable("newsArticle", it)
                Log.d(TAG, "bundle info: $it")
            }

            findNavController().navigate(R.id.action_savedNewsFragment2_to_articleFragment2, bundle)
        }
    }


}