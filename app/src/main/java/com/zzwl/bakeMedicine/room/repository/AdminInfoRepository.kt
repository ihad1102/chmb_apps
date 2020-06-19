package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.bakeMedicine.api.TobaccoApi

class AdminInfoRepository:BaseRepository() {
    fun getAdminInfo(groupId: Int) = NetworkBoundResult(
            {
                apiProvider.create(TobaccoApi::class.java).getAdminInfo(groupId )
            }
    )

}