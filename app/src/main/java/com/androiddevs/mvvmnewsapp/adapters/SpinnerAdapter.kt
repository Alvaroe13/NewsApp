package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.androiddevs.mvvmnewsapp.R
import kotlinx.android.synthetic.main.flags_layout.view.*

class SpinnerAdapter( private val flag : IntArray)  : BaseAdapter(){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view =  LayoutInflater.from(parent?.context).inflate(R.layout.flags_layout, null)
        view.ivFlags.setImageResource(flag[position])
        return view
    }

    override fun getItem(position: Int): Any = flag[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = flag.size

}