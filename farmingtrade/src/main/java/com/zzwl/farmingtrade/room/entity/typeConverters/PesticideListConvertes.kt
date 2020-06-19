package com.zzwl.farmingtrade.room.entity.typeConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zzwl.farmingtrade.room.entity.common.ChildrenP1

class PesticideListConvertes {
    @TypeConverter
    fun fromPesticideListToString(value: List<ChildrenP1>) = Gson().toJson(value)

    @TypeConverter
    fun stringToPesticideList(value: String): List<ChildrenP1> {
        val type = object : TypeToken<List<ChildrenP1>>() {}.type
        return Gson().fromJson<List<ChildrenP1>>(value, type)
    }
}