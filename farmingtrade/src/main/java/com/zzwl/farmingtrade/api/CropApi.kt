package com.zzwl.farmingtrade.api

import com.g.base.api.BaseResult
import com.zzwl.farmingtrade.room.entity.common.CropTypeEntity
import com.zzwl.farmingtrade.room.entity.remote.FollowedCropEntity
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by qhn on 2018/1/8.
 */
interface CropApi {
    @GET("crop/category")
    fun getCropType(): Observable<BaseResult<List<CropTypeEntity>?>>

    @GET("crop/list/{categoryId}")
    fun getCrop(@Path("categoryId") categoryId: Int): Observable<BaseResult<List<FollowedCropEntity>?>>

    @FormUrlEncoded
    @POST("crop/collect")
    fun collectCrops(@FieldMap map: Map<String, String>): Observable<BaseResult<Any?>>
}