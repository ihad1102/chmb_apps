package com.zzwl.question.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.api.UserApi


/**
 * Created by qhn on 2017/11/18.
 */
class BasicInfoRepository : BaseRepository() {
    //获取基本信息
    fun getUserInfo(force: Boolean) =
            NetworkBoundResult(
                    {
                        apiProvider.create(UserApi::class.java)
                                .getUserInfo()
                    }
            )


}