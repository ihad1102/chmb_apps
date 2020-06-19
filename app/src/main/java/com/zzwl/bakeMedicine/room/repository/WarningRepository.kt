package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.bakeMedicine.api.WarningApi

class WarningRepository : BaseRepository() {
    fun getCurrentWarning(houseId: String?,
                          pageNum: String,
                          pageSize: String,
                          keyword: String?,
                          houseGroupId: String) = NetworkBoundResult({
        apiProvider.create(WarningApi::class.java).getCurrentWarningList(
                mapOf(
                        Pair("pageNum", pageNum),
                        Pair("pageSize", pageSize),
                        Pair("houseGroupId", houseGroupId)),
                houseId, keyword
        )
    })

    fun getHistoryWarning(houseId: String?,
                          pageNum: String,
                          pageSize: String,
                          beginTime: String,
                          endTime: String,
                          houseGroupId: String,
                          keyword: String?) = NetworkBoundResult({
        apiProvider.create(WarningApi::class.java).getHistoryWarningList(
                mapOf(
                        Pair("pageNum", pageNum),
                        Pair("pageSize", pageSize),
                        Pair("beginTime", beginTime),
                        Pair("endTime", endTime),
                        Pair("houseGroupId", houseGroupId))
                , houseId, keyword
        )
    })
}