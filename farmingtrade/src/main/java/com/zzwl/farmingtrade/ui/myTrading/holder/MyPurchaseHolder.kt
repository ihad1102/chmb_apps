package com.zzwl.farmingtrade.ui.myTrading.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemMyPurchaseBinding
import com.zzwl.farmingtrade.room.entity.remote.MyPurchaseItem

class MyPurchaseHolder(val myPurchaseItem: MyPurchaseItem, val tempStatusObs: ObservableField<Boolean>) : BaseItem<FarmingItemMyPurchaseBinding>() {
    override val layoutId: Int = R.layout.farming_item_my_purchase
    val isShowUnit = myPurchaseItem.price != "面议"

}