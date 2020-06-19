package com.zzwl.farmingtrade.room.entity.typeConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.zzwl.farmingtrade.room.entity.common.HeWeatherREntity

class HeWeatherConverters {
    @TypeConverter
    fun fromHeWeatherToString(value: HeWeatherREntity) = Gson().toJson(value)

    @TypeConverter
    fun stringToHeWeather(value: String) = Gson().fromJson(value, HeWeatherREntity::class.java)
}