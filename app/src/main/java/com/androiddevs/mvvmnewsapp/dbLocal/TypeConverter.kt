package com.androiddevs.mvvmnewsapp.dbLocal

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Source

class TypeConverter {

    //from source to String
    @TypeConverter
    fun convertToString(src : Source) : String {
        return src.name
    }

    //from String to Source
    @TypeConverter
    fun convertToSource( name : String): Source{
        return Source(name, name)
    }

}