package com.g.base.room.typeConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class GroupIdConverters {
    @TypeConverter
    fun fromGroupIdToString(value: List<Int>) = Gson().toJson(value)

    @TypeConverter
    fun stringTofromGroupId(value: String): List<Int> {
        val type =object :TypeToken<List<Int>>(){}.type
        return Gson().fromJson<List<Int>>(value, type)
    }
}