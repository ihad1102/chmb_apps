package com.zzwl.question.ui.question.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemOnlineBinding

class OnlineHolder(val filterObs: ObservableField<String>) : BaseItem<QuestionItemOnlineBinding>() {
    override val layoutId: Int = R.layout.question_item_online
}