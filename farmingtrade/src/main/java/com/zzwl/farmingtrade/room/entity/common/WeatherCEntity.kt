package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.zzwl.farmingtrade.room.entity.typeConverters.HeWeatherConverters


@Entity
@TypeConverters(HeWeatherConverters::class)
data class HeWeatherCEntity(
        @PrimaryKey
        var id: String = "",
        var updateDate: Long = 0,
        var data: HeWeatherREntity = HeWeatherREntity()
)

data class OutWeatherREntity(
        @SerializedName("HeWeather6") var heWeather6: List<HeWeatherREntity> = listOf()
)

data class HeWeatherREntity(
        @SerializedName("basic") var basic: Basic = Basic(),
        @SerializedName("daily_forecast") var dailyForecast: List<DailyForecast> = listOf(),
        @SerializedName("hourly") var hourly: List<Hourly> = listOf(),
        @SerializedName("now") var now: Now = Now(),
        @SerializedName("status") var status: String = "",
        @SerializedName("update") var update: Update = Update()
)

data class Hourly(
        @SerializedName("cloud") var cloud: String = "",
        @SerializedName("cond_code") var condCode: String = "",
        @SerializedName("cond_txt") var condTxt: String = "",
        @SerializedName("hum") var hum: String = "",
        @SerializedName("pop") var pop: String = "",
        @SerializedName("pres") var pres: String = "",
        @SerializedName("time") var time: String = "",
        @SerializedName("tmp") var tmp: String = "",
        @SerializedName("wind_deg") var windDeg: String = "",
        @SerializedName("wind_dir") var windDir: String = "",
        @SerializedName("wind_sc") var windSc: String = "",
        @SerializedName("wind_spd") var windSpd: String = ""
)

data class Now(
        @SerializedName("cond_code") var condCode: String = "",
        @SerializedName("cond_txt") var condTxt: String = "",
        @SerializedName("fl") var fl: String = "",
        @SerializedName("hum") var hum: String = "",
        @SerializedName("pcpn") var pcpn: String = "",
        @SerializedName("pres") var pres: String = "",
        @SerializedName("tmp") var tmp: String = "",
        @SerializedName("vis") var vis: String = "",
        @SerializedName("wind_deg") var windDeg: String = "",
        @SerializedName("wind_dir") var windDir: String = "",
        @SerializedName("wind_sc") var windSc: String = "",
        @SerializedName("wind_spd") var windSpd: String = ""
)

data class DailyForecast(
        @SerializedName("cond_code_d") var condCodeD: String = "",
        @SerializedName("cond_code_n") var condCodeN: String = "",
        @SerializedName("cond_txt_d") var condTxtD: String = "",
        @SerializedName("cond_txt_n") var condTxtN: String = "",
        @SerializedName("sr") var sr: String = "",
        @SerializedName("ss") var ss: String = "",
        @SerializedName("date") var date: String = "",
        @SerializedName("hum") var hum: String = "",
        @SerializedName("pcpn") var pcpn: String = "",
        @SerializedName("pop") var pop: String = "",
        @SerializedName("pres") var pres: String = "",
        @SerializedName("tmp_max") var tmpMax: String = "",
        @SerializedName("tmp_min") var tmpMin: String = "",
        @SerializedName("uv_index") var uvIndex: String = "",
        @SerializedName("vis") var vis: String = "",
        @SerializedName("wind_deg") var windDeg: String = "",
        @SerializedName("wind_dir") var windDir: String = "",
        @SerializedName("wind_sc") var windSc: String = "",
        @SerializedName("wind_spd") var windSpd: String = ""
)

data class Basic(
        @SerializedName("cid") var cid: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("parent_city") var parentCity: String = "",
        @SerializedName("admin_area") var adminArea: String = "",
        @SerializedName("cnty") var cnty: String = "",
        @SerializedName("lat") var lat: String = "",
        @SerializedName("lon") var lon: String = "",
        @SerializedName("tz") var tz: String = ""
)

data class Update(
        @SerializedName("loc") var loc: String = "",
        @SerializedName("utc") var utc: String = ""
)