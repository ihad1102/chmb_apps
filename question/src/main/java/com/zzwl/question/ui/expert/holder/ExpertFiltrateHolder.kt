package com.zzwl.question.ui.expert.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemExpertFiltrateBinding

/**
 * Created by qhn on 2018/1/3.
 *
 */
class ExpertFiltrateHolder(
        val text: ObservableField<String>,
        val isSelectedObs: ObservableField<Boolean>,
        val id: String = "") : BaseItem<QuestionItemExpertFiltrateBinding>() {
    override val layoutId: Int = R.layout.question_item_expert_filtrate

}