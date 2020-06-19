package com.zzwl.question.ui.expert.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyExpertBinding
import com.zzwl.question.room.entity.common.ExpertCEntity

/**
 * Created by qhn on 2018/1/3.
 */
class ExpertHolder(val expert: ExpertCEntity?, val isShowSelect: Boolean = true, val isCheck: ObservableField<Boolean>? = null) : BaseItem<QuestionItemRcyExpertBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_expert
    val errorImg = ObservableField(R.drawable.question_ic_default_head_portrait_man)
    override fun onBind(binding: QuestionItemRcyExpertBinding) {
        super.onBind(binding)
        binding.checkBox.setOnClickListener {
            isCheck?.set(binding.checkBox.isChecked)
            onClickListener?.invoke(binding.checkBox, this)
        }
    }
}