package com.zzwl.question.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.api.ExpertApi
import com.zzwl.question.api.QuestionApi
import com.zzwl.question.viewModel.MyQuestion

/**
 * Created by qhn on 2018/1/9.
 */
class MyselfRepository : BaseRepository() {
    fun getAnswerList(userId: Int, pageNum: Int, pageSize: Int) =
            NetworkBoundResult(
                    {
                        apiProvider.create(QuestionApi::class.java)
                                .getMyAnswerList(mapOf(
                                        Pair("userId", userId),
                                        Pair("pageNum", pageNum),
                                        Pair("pageSize", pageSize)
                                ))
                    }
            )

    //  我的问题,
    fun getQuestionList(myQuestion: MyQuestion) =
            NetworkBoundResult(
                    {
                        apiProvider.create(QuestionApi::class.java)
                                .getQuestions(mapOf(
                                        Pair("userId", myQuestion.userId),
                                        Pair("pageNum", myQuestion.pageNum),
                                        Pair("pageSize", myQuestion.pageSize)))
                    }
            )

    fun getReplyList(pageNum: Int, pageSize: Int = 10) =
            NetworkBoundResult(
                    {
                        apiProvider.create(QuestionApi::class.java)
                                .getReplied(pageNum, pageSize)
                    }
            )

    fun getMyFollowedExpert(pageNum: Int, pageSize: Int = 10) =
            NetworkBoundResult(
                    {
                        apiProvider.create(ExpertApi::class.java)
                                .getFollowedExpert(
                                        mapOf(
                                                Pair("pageNum", pageNum.toString()),
                                                Pair("pageSize", pageSize.toString())
                                        )
                                )
                    }
            )

}