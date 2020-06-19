package com.g.base.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.g.base.room.entity.TokenEntity

/**
 * Created by G on 2017/11/6 0006.
 */
@Dao()
interface TokenDao {
    companion object {
        const val TABLE_NAME = "TokenEntity"
    }

    @Query("SELECT * FROM $TABLE_NAME LIMIT 1")
    fun getTokenLive(): LiveData<TokenEntity?>

    @Query("SELECT * FROM $TABLE_NAME LIMIT 1")
    fun getToken(): TokenEntity?

    @Query("DELETE FROM $TABLE_NAME")
    fun nukeTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inset(vararg d: TokenEntity?)
}