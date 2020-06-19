package com.zzwl.farmingtrade.api

import com.g.base.api.BaseResult
import io.reactivex.Observable
import retrofit2.http.*

interface TradePayApi {
    @Multipart
    @POST("/deal/pay")
    fun getPayParams(@Part(value = "dealId") dealId: String) : Observable<BaseResult<String?>>

    @GET("/deal/query_deal_status")
    fun asyncStatus(@Query(value = "id") dealId: String) : Observable<BaseResult<Any?>>
}