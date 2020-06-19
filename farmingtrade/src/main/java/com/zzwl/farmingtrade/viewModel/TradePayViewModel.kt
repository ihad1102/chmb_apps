package com.zzwl.farmingtrade.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.repository.TradePayRepository

class TradePayViewModel : BaseViewModel() {

    val payRepository by lazy { TradePayRepository() }

    fun getPayParams(dealId: String) = payRepository.getPayParams(dealId)
    fun asyncStatus(dealId: String) = payRepository.asyncStatus(dealId)

}