package com.zzwl.farmingtrade.api

import com.g.base.api.BaseResult
import com.google.gson.JsonObject
import com.zzwl.farmingtrade.room.entity.common.AddressItemCEntity
import com.zzwl.farmingtrade.room.entity.common.AddressRegionCEntity
import com.zzwl.farmingtrade.room.entity.remote.PickupPointREntity
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by G on 2017/11/16 0016.
 */
interface AddressApi {
    @GET("user/address")
    fun getAllAddress(): Observable<BaseResult<List<AddressItemCEntity>>>

    @PUT("user/address")
    fun upDateAddress(@Body jsonObject: JsonObject): Observable<BaseResult<Any?>>

    @POST("user/address")
    fun insertAddress(@Body jsonObject: JsonObject): Observable<BaseResult<Any?>>

    @DELETE("user/address/{id}")
    fun deleteAddress(@Path("id") id: String): Observable<BaseResult<Any?>>

    @GET("user/get_region/{type}/{parent}")
    fun getRegion(@Path("type") type: String, @Path("parent") parent: String): Observable<BaseResult<List<AddressRegionCEntity>>>

    @GET("user/get_pickup_point/{parent}")
    fun getPickupPoint(@Path("parent") parent: String): Observable<BaseResult<List<PickupPointREntity>>>
}