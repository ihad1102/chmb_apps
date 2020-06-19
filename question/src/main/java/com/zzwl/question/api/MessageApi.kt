package com.zzwl.question.api

import com.g.base.api.BaseResult
import com.zzwl.question.room.entity.remote.MessageREntity
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by G on 2017/11/13 0013.
 */
interface MessageApi {
    @FormUrlEncoded
    @POST("notify/list")
    fun getMessageList(@FieldMap map: Map<String, String>): Observable<BaseResult<List<MessageREntity>?>>

    @GET("notify/count")
    fun getMessageCount(): Observable<BaseResult<Int?>>

    @GET("notify/read")
    fun markMessageRead(@Query("id") id: String): Observable<BaseResult<Any?>>

    @GET("notify/type/{type}")
    fun markAllMessageRead(@Path("type") type: String): Observable<BaseResult<Any?>>

    @DELETE("notify/{id}")
    fun delMessage(@Path("id") id: String): Observable<BaseResult<Any?>>
}