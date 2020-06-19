package com.zzwl.farmingtrade.api


import com.g.base.api.BaseResult
import com.zzwl.farmingtrade.room.entity.remote.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface PurchaseApi {
    @GET("purchase/list")
    fun getBuyHallList(@QueryMap map: Map<String, String>, @Query("keyword") keyword: String?): Observable<BaseResult<PurchaseEntity?>>

    @GET("purchase/keywords")
    fun getHotSearchWord(): Observable<BaseResult<List<String>?>>

    @GET("purchase/detail")
    fun getPurchaseDetail(@Query("id") id: Int): Observable<BaseResult<PurchaseDetailsEntity>>

    //发布采购信息
    @POST("purchase/add")
    fun postPurchase(@QueryMap map: Map<String, String>, @Query("price") price: String?): Observable<BaseResult<Any?>>

    //发起交易
    @Multipart
    @POST("deal/seller_create")
    fun createPurchase(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>

    @GET("purchase/mine")
    fun getMyPurchase(@Query("pageNum") pageNum: String, @Query("pageSize") pageSize: String): Observable<BaseResult<MyPurchaseEntity?>>

    //我的交易
    @GET("deal/mine")
    fun getMyTrading(@Query("pageNum") pageNum: String, @Query("pageSize") pageSize: String): Observable<BaseResult<MyTradeEntity?>>

    //上架
    @GET("purchase/putaway")
    fun putawayPurchase(@Query("id") id: String): Observable<BaseResult<Any?>>

    //下架
    @GET("purchase/sold_out")
    fun unShelvePurchase(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("deal/cancel")
    fun cancelTrade(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("deal/confirm")
    fun confirmTrade(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("purchase/delete")
    fun deletePurchase(@Query("id") id: String): Observable<BaseResult<Any?>>


    @POST("purchase/update")
    fun updatePurchase(@QueryMap map: Map<String, String>, @Query("price") price: String?, @Query("doing") doing: Boolean = false): Observable<BaseResult<Any?>>

    @GET("purchase/goto_update")
    fun getPurchase(@Query("id") id: String): Observable<BaseResult<UpdatePurchaseEntity?>>

}