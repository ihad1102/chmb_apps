package com.zzwl.farmingtrade.ui.myTrading.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemMySupplierBinding
import com.zzwl.farmingtrade.room.entity.remote.MySupplierItem

class MySupplierHolder(val imgUrl: String,
                       val mySupplierItem: MySupplierItem,
                       val statusObs: ObservableField<String>,
                       val tempBtnObs: ObservableField<String>,
                       val isShowBtn2Obs: ObservableField<Boolean>,
                       val isShowBtn3Obs: ObservableField<Boolean>) : BaseItem<FarmingItemMySupplierBinding>() {
    override val layoutId: Int = R.layout.farming_item_my_supplier
    val isShowUnit = mySupplierItem.price != "面议"

}