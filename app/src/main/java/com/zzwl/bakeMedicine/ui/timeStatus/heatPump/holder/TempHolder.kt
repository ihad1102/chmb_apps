package com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTempBinding

class TempHolder(val tempObs: ObservableField<String>) : BaseItem<ItemTempBinding>() {
    override val layoutId: Int = R.layout.item_temp
}