package com.zzwl.farmingtrade.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.repository.PurchaseRepository

class PurchaseDetailsViewModel : BaseViewModel() {
    private val repository by lazy { PurchaseRepository() }

    fun getPurchaseDetails(id:Int)=repository.getPurchaseDetail(id)
}