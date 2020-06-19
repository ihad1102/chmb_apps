package com.zzwl.farmingtrade.room.entity.typeConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zzwl.farmingtrade.room.entity.common.Children

class FertilizerListConvertes {
    @TypeConverter
    fun fromFertilizerListToString(value: List<Children>) = Gson().toJson(value)

    @TypeConverter
    fun stringToFertilizerList(value: String): List<Children> {
        val type = object : TypeToken<List<Children>>() {}.type
        return Gson().fromJson<List<Children>>(value, type)
    }
}