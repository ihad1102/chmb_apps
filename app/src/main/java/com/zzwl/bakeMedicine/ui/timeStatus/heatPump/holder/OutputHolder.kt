package com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemOutputBinding

class OutputHolder(val machineNameObs:ObservableField<String>,val machineStatusObs: ObservableField<Boolean>):BaseItem<ItemOutputBinding>() {
    private val tempText=if (machineStatusObs.get()==true) "运行" else "关闭"
    val workStatusObs by lazy { ObservableField(tempText) }
    override val layoutId: Int= R.layout.item_output
}