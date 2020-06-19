package com.zzwl.question.api

import com.g.base.api.BaseResult
import com.zzwl.question.room.entity.common.ExpertCEntity
import com.zzwl.question.room.entity.common.ExpertInfoCEntity
import com.zzwl.question.room.entity.remote.ExpertTypeREntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Created by qhn on 2018/1/17.
 */
interface ExpertApi {
    //所有的专家
    @GET("expert/list")
    fun getExperts(@QueryMap map: Map<String, String?>): Observable<BaseResult<List<ExpertCEntity>?>>

    //我关注的专家
    @GET("expert/collect_list")
    fun getFollowedExpert(@QueryMap map: Map<String, String?>): Observable<BaseResult<List<ExpertCEntity>?>>

    @GET("expert/detail")
    fun getExpertInfo(@Query("id") id: String): Observable<BaseResult<ExpertInfoCEntity?>>

//    @GET("expert/collect/{id}/{isFollow}")
//    fun followExpert(@Path("id") id: String, @Path("isFollow") isFollow: Boolean): Observable<BaseResult<Any?>>

    @GET("expert/collect")
    fun followExpert(@Query("expertId") id: String): Observable<BaseResult<Any?>>

    @GET("expert/cancel_collect")
    fun cancelFollowExpert(@Query("expertId") id: String): Observable<BaseResult<Any?>>

    //专家类型
    @GET("user/getVerifiedCategorys")
    fun getExpertType(): Observable<BaseResult<List<ExpertTypeREntity>?>>


    //专家二维码
    @GET("question/getTimestamp")
    fun getTimeStamp(@Query("expertId") expertId: String): Observable<BaseResult<String?>>


    //提交专家评分
    @POST("question/add_score")
    fun commitGrade(@Query("expertId") expertId: Int, @Query("score") score: Int, @Query("commit") commit: String?, @Query("label") label: String?, @Query("timestamp") timestamp: String?): Observable<BaseResult<Any?>>

}