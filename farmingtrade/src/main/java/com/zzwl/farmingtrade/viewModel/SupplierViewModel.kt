package com.zzwl.farmingtrade.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.entity.remote.FarmingProductItem
import com.zzwl.farmingtrade.room.repository.SupplierRepository

class SupplierViewModel : BaseViewModel() {
    val repository by lazy { SupplierRepository() }
    private val farmingProductObs by lazy { MutableLiveData<FarmingProduct>() }
    val limit = 10
    var executedOrderBy = "quantity-desc"
    var choiceCropId = ""
    var farmingProductList: ArrayList<FarmingProductItem>? = null
    fun operateSupplier(pageNum: String, keyword: String? = null, cropId: String, orderBy: String) {
        farmingProductObs.value = FarmingProduct(pageNum, limit.toString(), keyword, cropId, orderBy)
    }

    fun obsFarmingProduct() = farmingProductObs.switchMap {
        repository.getFarmingProductList(it.pageNum, it.pageSize, it.keyword, it.orderBy, it.cropId)
    }

    fun getSupplierDetails(id: String) =
            repository.getFarmingProductDetails(id)


}

data class FarmingProduct(val pageNum: String, val pageSize: String, val keyword: String? = null, val cropId: String, val orderBy: String)