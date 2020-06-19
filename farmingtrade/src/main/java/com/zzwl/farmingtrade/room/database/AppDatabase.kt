package com.zzwl.farmingtrade.room.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.g.base.appContent
import com.zzwl.farmingtrade.BuildConfig
import com.zzwl.farmingtrade.room.dao.*
import com.zzwl.farmingtrade.room.entity.common.*
import com.zzwl.farmingtrade.room.entity.remote.FollowedCropEntity

/**
 * Created by G on 2017/11/6 0006.
 */

@Database(version = BuildConfig.VERSION_CODE,
        exportSchema = false,
        entities =
        [AddressItemCEntity::class,
            AddressRegionCEntity::class,
            BlockCEntity::class,
            CropCEntity::class,
            LogCEntity::class,
            CropBlockRelationLEntity::class,
            CropTypeEntity::class,
            FollowedCropEntity::class
        ])

abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DbName = "AppDatabase"
        val instant: AppDatabase by lazy {
            val databaseBuilder = Room.databaseBuilder<AppDatabase>(appContent, AppDatabase::class.java, DbName)
            databaseBuilder.fallbackToDestructiveMigration().allowMainThreadQueries().build()
        }
    }

    abstract fun getAddressDao(): AddressDao
    abstract fun getFarmingDao(): FarmingDao
}

