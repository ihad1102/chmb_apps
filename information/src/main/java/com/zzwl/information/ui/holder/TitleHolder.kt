package com.zzwl.information.ui.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoItemWikiDetailsTitleBinding

class TitleHolder(val title: String, val content: String) : BaseItem<InfoItemWikiDetailsTitleBinding>() {
    override val layoutId: Int = R.layout.info_item_wiki_details_title
}