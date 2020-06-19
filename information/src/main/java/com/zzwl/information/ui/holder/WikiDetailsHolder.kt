package com.zzwl.information.ui.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoItemWikiDetailsBinding

class WikiDetailsHolder(val head: String, val content: String) : BaseItem<InfoItemWikiDetailsBinding>() {
    override val layoutId: Int = R.layout.info_item_wiki_details
}