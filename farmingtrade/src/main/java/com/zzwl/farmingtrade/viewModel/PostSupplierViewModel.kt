package com.zzwl.farmingtrade.viewModel

import android.databinding.ObservableField
import com.g.base.common.apiProvider
import com.g.base.room.repository.NetworkBoundResult
import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.api.SupplierApi
import com.zzwl.farmingtrade.room.entity.remote.UpdateSupplierEntitiy
import com.zzwl.farmingtrade.room.repository.SupplierRepository

class PostSupplierViewModel : BaseViewModel() {
    val supplierRepository by lazy { SupplierRepository() }
    val maxSelect: ObservableField<Int> = ObservableField(3)
    var updateSupplierEntitiy: UpdateSupplierEntitiy? = null
    fun getSupplier(id: String) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .getSupplier(id)
    })
}