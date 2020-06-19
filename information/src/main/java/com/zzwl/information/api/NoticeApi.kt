package com.zzwl.information.api

import com.g.base.api.BaseResult
import com.zzwl.information.room.entity.NoticeEntity
import com.zzwl.information.room.entity.WikiDetailEntity
import com.zzwl.information.room.entity.WikiEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeApi {
    @GET("notice/list")
    fun getNotice(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): Observable<BaseResult<NoticeEntity?>>

    @GET("encyclopedia/list")
    fun getWiki(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int, @Query("category") category: Int): Observable<BaseResult<List<WikiEntity>?>>

    @GET("encyclopedia/detail")
    fun getWikiDetail(@Query("id") id: Int): Observable<BaseResult<WikiDetailEntity>>
}