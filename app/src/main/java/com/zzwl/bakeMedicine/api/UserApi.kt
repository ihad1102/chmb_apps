package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.g.base.room.entity.TokenEntity
import com.zzwl.bakeMedicine.room.entity.remote.GroupEntity
import com.zzwl.bakeMedicine.room.entity.remote.UserInfoEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface UserApi {
    @POST("user/login")
    fun login(@Query("tel") telephone: String, @Query("password") password: String): Observable<BaseResult<TokenEntity?>>

    @GET("user/user_info")
    fun getUserInfo(): Observable<BaseResult<UserInfoEntity?>>

    @GET("user/logout")
    fun logout(): Observable<BaseResult<Any?>>


    @POST("user/verification_code")
    fun getCode(@Query("mobile") mobile: String, @Query("type") type: Int): Observable<BaseResult<Any?>>

    @POST("user/register")
    fun register(@QueryMap map: Map<String, String>): Observable<BaseResult<TokenEntity?>>

    @GET("user/join_house_group")
    fun joinHouseGroup(@Query("groupId") groupId: Int): Observable<BaseResult<GroupEntity?>>
}