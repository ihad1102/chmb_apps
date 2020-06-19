package com.zzwl.question.ui.expert.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemExpertFiltrateBinding


/**
 * Created by qhn on 2018/1/3.
 *  isTitle ? 标题 : 可点击的textview,
 */
class ExpertDistrictFiltrateHolder(private val isTitle: Boolean, val text: String) : BaseItem<QuestionItemExpertFiltrateBinding>() {
    override val layoutId: Int = R.layout.question_item_expert_filtrate
    override fun onBind(binding: QuestionItemExpertFiltrateBinding) {
    }
}