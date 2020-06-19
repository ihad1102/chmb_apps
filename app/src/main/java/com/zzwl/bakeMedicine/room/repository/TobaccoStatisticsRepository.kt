package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.common.apiProviderMock
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.bakeMedicine.api.TobaccoApi

class TobaccoStatisticsRepository : BaseRepository() {
    fun getTobaccoStatistics(groupId: Int) = NetworkBoundResult(
            {
                apiProvider.create(TobaccoApi::class.java).getTobaccoList(groupId)
            }
    )

    fun getTobaccoStatus(houseId: Int) = NetworkBoundResult({
        apiProvider.create(TobaccoApi::class.java).getTobaccoStatus(houseId)
    })

    fun getHotPumpTobaccoStatus(houseId: Int) = NetworkBoundResult({
        apiProviderMock.create(TobaccoApi::class.java).getHotPumpTobaccoStatus()
    })

    fun getTobaccoCurve(houseId: Int, leafOption: Int) = NetworkBoundResult({
        apiProvider.create(TobaccoApi::class.java).getTobaccoCurve(houseId, leafOption)
    })
}