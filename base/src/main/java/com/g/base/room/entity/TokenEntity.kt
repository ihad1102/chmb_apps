package com.g.base.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.g.base.room.typeConverters.GroupIdConverters

/**
 * Created by G on 2017/8/18 0018.
 */
@Entity
@TypeConverters(GroupIdConverters::class)
data class TokenEntity(
        @PrimaryKey
        var userId: String = "",
        var token: String = "",
        var createTime: Long = 0L,
        var groupIdList: List<Int>? = listOf()
)

