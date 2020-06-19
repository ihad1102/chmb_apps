package com.zzwl.bakeMedicine.ui.timeStatus.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemPullDownBinding

class PullDownHolder(val text: String,val id: Int) : BaseItem<ItemPullDownBinding>() {
    override val layoutId: Int = R.layout.item_pull_down
}