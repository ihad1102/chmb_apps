package com.zzwl.question.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.api.ExpertApi
import com.zzwl.question.room.entity.common.ExpertInfoCEntity

class ExpertInfoRepository : BaseRepository() {

    //获取专家信息 缓存3天//缓存去掉.
    fun getExpertDetail(id: String, force: Boolean) =
            NetworkBoundResult(
                    {
                        apiProvider.create(ExpertApi::class.java)
                                .getExpertInfo(id)
                    }
//                    ,
//                    {
//                        AppDatabase.instant.getExpertInfoDao().getById(id)
//                    },
//                    {
//                        it == null || System.currentTimeMillis() - it.createDate >= 1000L * 60L * 24L * 1L || force
//                    },
//                    {
//                        it ?: return@NetworkBoundResult
//                        it.createDate = System.currentTimeMillis()
//                        AppDatabase.instant.runDataBaseTransition {
//                            getExpertInfoDao().inertData(it)
//                        }
//                    }
            )

    fun followExpert(v: ExpertInfoCEntity) = NetworkBoundResult(
            {
                if (v.isFollow)
                    apiProvider.create(ExpertApi::class.java)
                            .followExpert(v.id)
                else
                    apiProvider.create(ExpertApi::class.java)
                            .cancelFollowExpert(v.id)
            }
    )
}