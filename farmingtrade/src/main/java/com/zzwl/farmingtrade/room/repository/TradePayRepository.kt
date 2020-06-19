package com.zzwl.farmingtrade.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.farmingtrade.api.TradePayApi

class TradePayRepository : BaseRepository() {
    fun getPayParams(dealId: String) = NetworkBoundResult(
            {
                apiProvider.create(TradePayApi::class.java).getPayParams(dealId)
            })

    fun asyncStatus(dealId: String) = NetworkBoundResult(
            {
                apiProvider.create(TradePayApi::class.java).asyncStatus(dealId)
            })
}