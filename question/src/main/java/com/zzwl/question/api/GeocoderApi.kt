package com.zzwl.question.api

import com.zzwl.question.room.entity.GeocoderREntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocoderApi {
    @GET("http://api.map.baidu.com/geocoder/v2/")
    fun baiduGeocder(@Query("location") location: String,
                     @Query("output") output: String = "json",
                     @Query("ak") ak: String = "OHU6QWC1K5S7nRamxG7wN18XdCM5SIEG")
            : Observable<GeocoderREntity>
}