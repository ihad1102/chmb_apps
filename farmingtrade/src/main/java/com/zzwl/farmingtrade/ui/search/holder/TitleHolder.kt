package com.zzwl.farmingtrade.ui.search.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemSearchTitleBinding


class TitleHolder(val title: String, var delObs: ObservableField<Boolean>? = null) : BaseItem<FarmingItemSearchTitleBinding>() {
    override val layoutId: Int = R.layout.farming_item_search_title
}