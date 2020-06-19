package com.zzwl.bakeMedicine.model.bindView

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemSingleTextBinding

/**
 * Created by G on 2017/8/21 0021.
 */
class SingleTextHolder(var text: String?) : BaseItem<ItemSingleTextBinding>() {
    override val layoutId: Int = R.layout.item_single_text
}
