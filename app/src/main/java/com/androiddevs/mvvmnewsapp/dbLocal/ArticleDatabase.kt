package com.androiddevs.mvvmnewsapp.dbLocal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.DATABASE_VERSION

@Database( entities = [Article::class] , version = DATABASE_VERSION)
@TypeConverters(TypeConverter::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract  fun getDao() : ArticleDao

    companion object{

        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke( mContext : Context) = instance ?: synchronized(LOCK){
            //can be access by any thread at the same time
            //if null we create db
            instance ?: buildDataBase(mContext).also { instance = it}
        }

        private fun buildDataBase(mContext: Context) =
            Room.databaseBuilder(
                mContext.applicationContext,
                ArticleDatabase::class.java,
                "Articles.db"
            ).build()



    }


}