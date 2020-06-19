package com.zzwl.question.api

import com.g.base.api.BaseResult
import com.zzwl.question.room.entity.remote.BannerREntity
import io.reactivex.Observable
import retrofit2.http.GET

interface BannerApi {
    @GET("advertis/getAll")
    fun getBanner(): Observable<BaseResult<List<BannerREntity?>>>
}