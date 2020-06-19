package com.zzwl.farmingtrade.ui.search.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemSearchTagBinding


class TagHolder(val tag: String) : BaseItem<FarmingItemSearchTagBinding>() {
    override val layoutId: Int = R.layout.farming_item_search_tag
}