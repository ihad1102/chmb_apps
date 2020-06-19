package com.zzwl.farmingtrade.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.repository.PurchaseRepository
import com.zzwl.farmingtrade.room.repository.SupplierRepository

class SearchViewModel : BaseViewModel() {
    private val purchaseRepository by lazy { PurchaseRepository() }
    private val supplierRepository by lazy { SupplierRepository() }
    val wordList by lazy { ArrayList<String>() }
    fun getHotSearchWordByPurchase() = purchaseRepository.getHotSearchWord()
    fun getHotSearchWordBySupplier() = supplierRepository.getHotWord()
}