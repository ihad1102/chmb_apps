package com.zzwl.farmingtrade.ui.purchase.holder

import android.databinding.ObservableField
import com.g.base.extend.transformTime
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemBuyBinding
import com.zzwl.farmingtrade.room.entity.remote.PurchaseItem


class BuyHolder(val purchaseItem: PurchaseItem) : BaseItem<FarmingItemBuyBinding>() {
    val updateTimeObs by lazy { ObservableField<String>(purchaseItem.updateTime.transformTime()) }
    override val layoutId: Int = R.layout.farming_item_buy
    val isShowUnit = purchaseItem.price != "面议"

}