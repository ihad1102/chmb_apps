package com.zzwl.farmingtrade.api

import com.g.base.api.BaseResult
import com.zzwl.farmingtrade.room.entity.remote.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface SupplierApi {

    //发布农产品
    @Multipart
    @POST("crop_product/add")
    fun postFramingProduct(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>


    @GET("planting/list_select")
    fun farmingCropList(): Observable<BaseResult<List<FarmingCropEntity>?>>

    @GET("crop_product/keywords")
    fun getHotSearchWord(): Observable<BaseResult<List<String>?>>

    @GET("crop_product/list")
    fun getFarmingProduct(@QueryMap map: Map<String, String>, @Query("keyword") keyword: String?): Observable<BaseResult<FramingProductEntity?>>

    @GET("crop_product/detail")
    fun getFarmingProductDetails(@Query("id") id: String): Observable<BaseResult<SupplierEntity?>>

    //发起交易
    @Multipart
    @POST("deal/buyer_create")
    fun createSupplier(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>


    @GET("deal/subsidy")
    fun getSubsidiesRules(@Query("cropId") cropId: Int): Observable<BaseResult<List<SubsidiesRulesEntity>?>>

    @GET("crop_product/mine")
    fun getMySupplier(@Query("pageNum") pageNum: String, @Query("pageSize") pageSize: String): Observable<BaseResult<MySupplierEntity?>>

    @GET("crop_product/putaway")
    fun putawaySupplier(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("crop_product/sold_out")
    fun unShelveSupplier(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("crop_product/delete")
    fun deleteFarmingProduct(@Query("id") id: String): Observable<BaseResult<Any?>>

    @Multipart
    @POST("purchase/update")
    fun updateFramingProduct(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>

    @GET("crop_product/goto_update")
    fun getSupplier(@Query("id") id: String): Observable<BaseResult<UpdateSupplierEntitiy?>>

    @POST("user/update_intro")
    fun updateInfo(@Query("expertIntro") expertIntro: String): Observable<BaseResult<Any?>>

    @Multipart
    @POST("crop_product/update")
    fun updateProduct(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>

}