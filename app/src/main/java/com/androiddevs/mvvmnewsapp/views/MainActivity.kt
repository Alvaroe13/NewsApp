package com.androiddevs.mvvmnewsapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.dbLocal.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repositories.NewsRepo
import com.androiddevs.mvvmnewsapp.viewModels.NewsFeedViewModel
import com.androiddevs.mvvmnewsapp.viewModels.NewsVMProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel : NewsFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        setNavController()

    }

    //vm init here
    private fun initViewModel(){
        val repo = NewsRepo(ArticleDatabase(this))
        val newsVMProviderFactory = NewsVMProviderFactory(repo)
        viewModel = ViewModelProvider(this, newsVMProviderFactory).get(NewsFeedViewModel::class.java)
    }

    private fun setNavController(){
        //connect navGraph through by declaring this controller
        bottomNavigationView.setupWithNavController(navHostFragmentMain.findNavController())
    }
}
