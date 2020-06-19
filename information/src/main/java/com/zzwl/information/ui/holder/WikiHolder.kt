package com.zzwl.information.ui.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoItemWikiBinding

class WikiHolder(val title: String, val intro: String, val imageUrl: String, val id: Int) : BaseItem<InfoItemWikiBinding>() {
    override val layoutId: Int = R.layout.info_item_wiki
}