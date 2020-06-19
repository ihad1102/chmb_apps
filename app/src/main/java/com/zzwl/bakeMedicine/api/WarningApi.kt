package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.zzwl.bakeMedicine.room.entity.remote.CurrentWarningEntityList
import com.zzwl.bakeMedicine.room.entity.remote.HistoryWarningEntityList
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WarningApi {
    @POST("alarm/current_list")
    fun getCurrentWarningList(@QueryMap map: Map<String, String>, @Query("houseId") houseId: String?, @Query("keyword") keyword: String?): Observable<BaseResult<CurrentWarningEntityList?>>

    @POST("alarm/history_list")
    fun getHistoryWarningList(@QueryMap map: Map<String, String>, @Query("houseId") houseId: String?, @Query("keyword") keyword: String?): Observable<BaseResult<HistoryWarningEntityList?>>
}