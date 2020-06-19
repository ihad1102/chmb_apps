package com.zzwl.bakeMedicine.ui.warningHistory.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTitleWarningBinding

class TitleHolder(val isCurrentFragment:Boolean) :BaseItem<ItemTitleWarningBinding>() {
    override val layoutId: Int= R.layout.item_title_warning
}