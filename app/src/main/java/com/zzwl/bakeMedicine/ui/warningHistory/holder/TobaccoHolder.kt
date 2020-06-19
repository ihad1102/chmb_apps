package com.zzwl.bakeMedicine.ui.warningHistory.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTobaccoTextBinding

class TobaccoHolder(val tobaccoName: String, val houseId: Int?,val selectedIdObs: ObservableField<Int?>) : BaseItem<ItemTobaccoTextBinding>() {
    override val layoutId: Int = R.layout.item_tobacco_text
}