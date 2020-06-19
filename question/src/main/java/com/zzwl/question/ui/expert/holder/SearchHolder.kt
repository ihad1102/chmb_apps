package com.zzwl.question.ui.expert.holder

import android.databinding.ObservableField
import android.view.inputmethod.EditorInfo
import com.g.base.extend.toast
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemSearchExpertBinding
import com.zzwl.question.viewModel.ExpertViewModel

/**
 * Created by qhn on 2018/1/3.
 */
class SearchHolder(private val expertTypeObs: ObservableField<String?>,
                   private val cropIdObs: ObservableField<String?>,
                   private val cityIdObs: ObservableField<String?>,
                   private val expertViewModel: ExpertViewModel) : BaseItem<QuestionItemSearchExpertBinding>() {
    override val layoutId: Int = R.layout.question_item_search_expert
    override fun onBind(binding: QuestionItemSearchExpertBinding) {
        binding.edtSearch.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.edtSearch.text.toString().trim().isEmpty()) {
                    binding.root.context.toast("搜索内容不能为空")
                    return@setOnEditorActionListener true
                }
                expertViewModel.operateExpertList(0, null, expertTypeObs.get(), cityIdObs.get(), binding.edtSearch.text.toString(), cropIdObs.get())

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }
}