package com.g.base.room.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.g.base.appContent
import com.g.base.room.dao.TokenDao
import com.g.base.room.entity.TokenEntity

/**
 * Created by G on 2017/11/6 0006.
 */

@Database(version = 1, exportSchema = false, entities = [TokenEntity::class])
abstract class BaseDatabase : RoomDatabase() {
    companion object {
        private const val DbName = "BaseDatabase"
        val instant: BaseDatabase by lazy {
            val databaseBuilder = Room.databaseBuilder<BaseDatabase>(appContent, BaseDatabase::class.java, BaseDatabase.DbName)
            databaseBuilder.allowMainThreadQueries().build()
        }
    }

    abstract fun getTokenDao(): TokenDao
}

