package com.zzwl.question.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.api.ExpertApi
import com.zzwl.question.room.entity.common.ExpertCEntity


/**
 * Created by qhn on 2018/1/3.
 */
class ExpertRepository : BaseRepository() {
    //获取专家列表
    //isFollow true 获取关注的专家 false 获取全部的专家
    fun getExpertList(map: HashMap<String, String?>, isFollow: Boolean): NetworkBoundResult<List<ExpertCEntity>?> = NetworkBoundResult(
            {
                if (isFollow)
                    apiProvider.create(ExpertApi::class.java).getFollowedExpert(map)
                else
                    apiProvider.create(ExpertApi::class.java).getExperts(map)
            })

    fun searchExpert(keyword: String, pageSize: String, pageNum: String) = NetworkBoundResult({
        apiProvider.create(ExpertApi::class.java).getExperts(
                mapOf(
                        Pair("keyword", keyword),
                        Pair("pageSize", pageSize),
                        Pair("pageNum", pageNum)
                )
        )
    })

    fun getExpertType() = NetworkBoundResult({
        apiProvider.create(ExpertApi::class.java).getExpertType()
    })


}