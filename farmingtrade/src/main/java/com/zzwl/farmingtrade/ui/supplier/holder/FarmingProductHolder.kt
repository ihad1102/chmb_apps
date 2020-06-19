package com.zzwl.farmingtrade.ui.supplier.holder

import android.databinding.ObservableField
import com.g.base.extend.transformTime
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemSupplierHallBinding
import com.zzwl.farmingtrade.room.entity.remote.FarmingProductItem

class FarmingProductHolder(val farmingProductItem: FarmingProductItem, val imgUrl: String) : BaseItem<FarmingItemSupplierHallBinding>() {
    val updateTimeObs by lazy { ObservableField<String>(farmingProductItem.createTime.transformTime()) }
    private val tempUnit = if (farmingProductItem.price == "面议") "面议" else "${farmingProductItem.price}元/公斤"
    val unitObs by lazy { ObservableField<String>(tempUnit) }
    override val layoutId: Int = R.layout.farming_item_supplier_hall
}