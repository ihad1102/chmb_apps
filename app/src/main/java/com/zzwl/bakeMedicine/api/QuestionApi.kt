package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.zzwl.bakeMedicine.room.entity.common.FocusedCropsEntity
import com.zzwl.bakeMedicine.room.entity.common.QuestionDetailEntity
import com.zzwl.bakeMedicine.room.entity.remote.ImageEntity
import com.zzwl.bakeMedicine.room.entity.remote.MyAnswerREntity
import com.zzwl.bakeMedicine.room.entity.remote.QuestionEntity
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


/**
 * Created by qhn on 2018/1/2.
 */
interface QuestionApi {
    //mock


    @GET("followlist_crop")
    fun getFocusCrops(): Observable<BaseResult<List<FocusedCropsEntity?>?>>

    @GET("get_banner")
    fun getBanner(): Observable<BaseResult<List<String?>?>>


    @GET("question/detail")
    fun getQuestionDetail(@Query("id") questionId: Int, @Query("currentUserId") currentUserId: String?): Observable<BaseResult<QuestionDetailEntity?>>

    @GET("expert/reply_list")
    fun getMyAnswerList(@QueryMap map: Map<String, Int>): Observable<BaseResult<List<MyAnswerREntity?>?>>





    //really
    //我的问题 问题列表等,  和问题相关的所有接口(我的回复之外)
    @GET("question/list")
    fun getQuestions(@QueryMap map: Map<String, Int?>,
                     @Query("isResolve") isResolve: Boolean? = null,
                     @Query("keyword") keyword: String? = null,
                     @Query("cropId") cropId: Int? = null
    ): Observable<BaseResult<List<QuestionEntity?>?>>


    //评论
    @Multipart
    @POST("question/add_reply")
    fun uploadComment(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<Any?>>

    @Multipart
    @POST("question/post")
    fun uploadQuestion(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<QuestionEntity?>>

    //和接口命名相反,这里是回复评论
    @FormUrlEncoded
    @POST("question/add_comment")
    fun reply(@Field("replyId") replyId: Int, @Field("content") content: String): Observable<BaseResult<Any?>>

    //采纳
    @GET("question/adopt_reply/{replyId}")
    fun adopt(@Path("replyId") replyId: String): Observable<BaseResult<Any?>>

    //点赞or反对
    @POST("question/add_likelog")
    fun approveOrOppose(@Query("replyId") replyId: Int?, @Query("type") status: Int): Observable<BaseResult<Any?>>

    //我回复的问题列表
    @GET("question/replied_list")
    fun getReplied(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int): Observable<BaseResult<List<MyAnswerREntity?>?>>

    @Multipart
    @POST("file/image_list_upload")
    fun uploadImg(@Part files: Array<MultipartBody.Part>): Observable<BaseResult<List<ImageEntity>?>>
}