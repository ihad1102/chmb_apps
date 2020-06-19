package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.bakeMedicine.api.TobaccoApi

class HistoryCurveRepository : BaseRepository() {
    fun getHistoryCurveData(bakeId: Int) = NetworkBoundResult(
            {
                apiProvider.create(TobaccoApi::class.java).getHistoryCurveData(bakeId)
            }
    )

    fun getHistoryPullDownData(houseId: Int) = NetworkBoundResult(
            {
                apiProvider.create(TobaccoApi::class.java).getHousePullDown(houseId)
            }
    )
}