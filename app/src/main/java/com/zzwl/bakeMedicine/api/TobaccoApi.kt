package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.google.gson.JsonObject
import com.zzwl.bakeMedicine.room.entity.remote.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TobaccoApi {
    @GET("house/house_list")
    fun getTobaccoList(@Query("groupId") groupId: Int): Observable<BaseResult<TobaccoListInfoEntity?>>

    @GET("house/house_status")
    fun getTobaccoStatus(@Query("houseId") houseId: Int): Observable<BaseResult<TobaccoStatusEntity?>>

    @GET("hot_pump_house/house_status")
    fun getHotPumpTobaccoStatus(): Observable<BaseResult<HotPumpTobaccoStatusEntity?>>

    @GET("house/self_setting")
    fun getTobaccoCurve(@Query("houseId") houseId: Int, @Query("leafOption") leafOption: Int): Observable<BaseResult<List<TobaccoCurveEntity>?>>

    @GET("house/history")
    fun getHistoryCurveData(@Query("bakeId") bakeId: Int): Observable<BaseResult<List<HistoryCurveDataEntity>?>>

    @GET("house/bake_pull_down")
    fun getHousePullDown(@Query("houseId") houseId: Int): Observable<BaseResult<List<PullDownDataEntity>?>>

    @GET("house/role_info")
    fun getAdminInfo(@Query("groupId") groupId: Int): Observable<BaseResult<AdminInfoEntity>>

    @POST("house/all_curve")
    fun getAllCurve(@Query("houseId") houseId: Int): Observable<BaseResult<List<CurveEntity>>>

    @POST("house/set_curve")
    fun setCurve(@Body  jsonObject: JsonObject): Observable<BaseResult<Any?>>

}