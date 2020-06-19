package com.zzwl.farmingtrade.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.g.base.extend.switchMap
import com.zzwl.farmingtrade.room.entity.remote.PurchaseItem
import com.zzwl.farmingtrade.room.repository.PurchaseRepository


class PurchaseHallViewModel : ViewModel() {
    var purchaseItemList: ArrayList<PurchaseItem>? = null
    private val repository by lazy { PurchaseRepository() }
    private val purchaseLiveData by lazy { MutableLiveData<PurchaseData>() }
    val limit = 10
    var executedOrderBy: String = "quantity-desc"

    fun operatePurchase(pageNum: String, orderBy: String, keyWord: String? = null, cropId: String) {
        executedOrderBy = orderBy
        purchaseLiveData.value = PurchaseData(pageNum, limit.toString(), orderBy, keyWord, cropId)
    }

    fun obsPurchase() =
            purchaseLiveData.switchMap { repository.getPurchaseEntity(it.pageNum, it.pageSize, it.orderBy, it.keyWord, it.cropId) }

}

data class PurchaseData(val pageNum: String, val pageSize: String, val orderBy: String, val keyWord: String?, val cropId: String)