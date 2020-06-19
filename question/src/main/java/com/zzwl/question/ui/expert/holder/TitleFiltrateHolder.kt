package com.zzwl.question.ui.expert.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemTitleFiltrateBinding

class TitleFiltrateHolder(val content: String, val isDistrictButton: Boolean = false) : BaseItem<QuestionItemTitleFiltrateBinding>() {
    override val layoutId: Int = R.layout.question_item_title_filtrate
}