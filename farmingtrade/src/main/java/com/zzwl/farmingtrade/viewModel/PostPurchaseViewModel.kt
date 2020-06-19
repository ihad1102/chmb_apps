package com.zzwl.farmingtrade.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.entity.remote.UpdatePurchaseEntity
import com.zzwl.farmingtrade.room.repository.PurchaseRepository

class PostPurchaseViewModel : BaseViewModel() {
    private val purchaseRepository by lazy { PurchaseRepository() }
    var data: UpdatePurchaseEntity? = null
    fun getPurchase(id: String) =
            purchaseRepository.getPurchase(id)
}