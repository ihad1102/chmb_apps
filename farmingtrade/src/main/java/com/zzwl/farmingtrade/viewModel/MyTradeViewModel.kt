package com.zzwl.farmingtrade.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.entity.remote.MyPurchaseItem
import com.zzwl.farmingtrade.room.entity.remote.MySupplierItem
import com.zzwl.farmingtrade.room.entity.remote.MyTradeItem
import com.zzwl.farmingtrade.room.repository.PurchaseRepository
import com.zzwl.farmingtrade.room.repository.SupplierRepository

class MyTradeViewModel : BaseViewModel() {
    val supplierRepository by lazy { SupplierRepository() }
    val purchaseRepository by lazy { PurchaseRepository() }
    private val mySupplierObs by lazy { MutableLiveData<Pair<Int, Int>>() }
    private val myPurchaseObs by lazy { MutableLiveData<Pair<Int, Int>>() }
    private val myTradingObs by lazy { MutableLiveData<Pair<Int, Int>>() }

    val limit = 10
    var mySupplierList: ArrayList<MySupplierItem>? = null
    var myPurchaseList: ArrayList<MyPurchaseItem>? = null
    var myTradingList: ArrayList<MyTradeItem>? = null

    fun operateMySupplier(pageNum: Int) {
        mySupplierObs.value = Pair(pageNum, limit)
    }

    fun operateMyPurchase(pageNum: Int) {
        myPurchaseObs.value = Pair(pageNum, limit)
    }

    fun operateMyTrading(pageNum: Int) {
        myTradingObs.value = Pair(pageNum, limit)
    }

    fun obsMySupplier() = mySupplierObs.switchMap {
        supplierRepository.getMySupplier(it.first.toString(), it.second.toString())
    }

    fun obsMyPurchase() = myPurchaseObs.switchMap {
        purchaseRepository.getMyPurchase(it.first.toString(), it.second.toString())
    }

    fun obsMyTrading() = myTradingObs.switchMap {
        purchaseRepository.getMyTrading(it.first.toString(), it.second.toString())
    }
}