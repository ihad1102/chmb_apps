package com.zzwl.bakeMedicine.ui.home.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemNoticeBinding

class NoticeHolder(val title: String, val content: String, val date: String,val id:Int) : BaseItem<ItemNoticeBinding>() {
    override val layoutId: Int = R.layout.item_notice
}