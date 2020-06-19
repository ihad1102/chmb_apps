package com.zzwl.bakeMedicine.model.bindView

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.ClickAbleItem

/**
 * Created by G on 2017/11/13 0013.
 */
class TabItemBind(val title: String, val image: Int, val index: Int, val currentSelect: ObservableField<Int>, badge: Int = 0) : ClickAbleItem() {
    val badge = ObservableField(badge)
}