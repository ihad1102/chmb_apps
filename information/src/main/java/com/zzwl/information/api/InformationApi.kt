package com.zzwl.information.api

import com.g.base.api.BaseResult
import com.zzwl.information.room.entity.InfoEntity
import com.zzwl.information.room.entity.InformationEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface InformationApi {
    @GET("information/list")
    fun getInformation(@Query("pageSize") pageSize: Int, @Query("pageNum") pageNum: Int): Observable<BaseResult<InformationEntity?>>

    @GET("information/detail")
    fun getInformationDetails(@Query("id") id: Int): Observable<BaseResult<InfoEntity?>>
}