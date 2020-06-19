package com.zzwl.question.room.repository

import com.g.base.common.apiProvider
import com.g.base.common.apiProviderMock
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.api.BannerApi
import com.zzwl.question.api.QuestionApi
import okhttp3.MultipartBody


/**
 * Created by qhn on 2018/1/2.
 */
class QuestionRepository : BaseRepository() {

    fun getQuestionList(cropId: Int?, isResolve: Boolean?, pageNum: Int, PageSize: Int, keyword: String? = null) =
            NetworkBoundResult({
                apiProvider.create(QuestionApi::class.java)
                        .getQuestions(
                                mapOf(
                                        Pair("pageNum", pageNum),
                                        Pair("pageSize", PageSize)),
                                isResolve,
                                keyword,
                                cropId
                        )
            })


    //real
    fun getBanners() = NetworkBoundResult(
            {
                apiProviderMock.create(BannerApi::class.java).getBanner()
            }
    )

    //问题详情
    fun getQuestionDetail(id: Int, currentUserId: String?) =
            NetworkBoundResult(
                    {
                        apiProvider.create(QuestionApi::class.java)
                                .getQuestionDetail(id, currentUserId)
                    }
            )

    //评论列表
    fun getCommentList() =
            NetworkBoundResult(
                    {
                        apiProviderMock.create(QuestionApi::class.java)
                                .getComment()
                    }
            )


    //问题列表 really 首页
    fun getQuestions(pageNum: Int, pageSize: Int, isResolve: Boolean?) =
            NetworkBoundResult({
                apiProvider.create(QuestionApi::class.java)
                        .getQuestions(
                                mapOf(
                                        Pair("pageNum", pageNum),
                                        Pair("pageSize", pageSize)),
                                isResolve
                        )
            })


    //发布问题
    fun postQuestion(multipartBodys: Array<MultipartBody.Part>) =
            NetworkBoundResult({
                apiProvider.create(QuestionApi::class.java)
                        .uploadQuestion(multipartBodys)
            })
}