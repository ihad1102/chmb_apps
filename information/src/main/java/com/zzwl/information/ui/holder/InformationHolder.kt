package com.zzwl.information.ui.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoItemInformationBinding

class InformationHolder(val title: String, val name: String, val time: String, val id: Int, val imgUrl: String) : BaseItem<InfoItemInformationBinding>() {
    override val layoutId: Int = R.layout.info_item_information
}