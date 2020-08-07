package com.androiddevs.mvvmnewsapp.views.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsFeedViewModel
    val incomingInfo : ArticleFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        //store article info in this info val
        val info = incomingInfo.newsArticle

       showArticleInWebView(info)
       fabButtonPressed(info, view)

    }

    private fun showArticleInWebView(info: Article){
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(info.url)
        }
    }

    //when fab pressed save article in local cache
    private fun fabButtonPressed(info: Article, layout : View) {
        fabSaveArticle.setOnClickListener {
            viewModel.saveArticleInCache(info)
            Snackbar.make(layout, "article added to favorites", Snackbar.LENGTH_SHORT).show()

        }
    }



}