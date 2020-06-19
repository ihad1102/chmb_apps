package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.zzwl.bakeMedicine.room.entity.remote.MapEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {
    @GET("house/group_list")
    fun getMapList(@Query("userId")userId:Int): Observable<BaseResult<List<MapEntity>>>
}