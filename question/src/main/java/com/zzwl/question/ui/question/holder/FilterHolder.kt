package com.zzwl.question.ui.question.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemFilterQuestionBinding


class FilterHolder(val item: String) : BaseItem<QuestionItemFilterQuestionBinding>() {
    override val layoutId: Int = R.layout.question_item_filter_question
}