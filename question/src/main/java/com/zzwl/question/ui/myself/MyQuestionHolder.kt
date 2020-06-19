package com.zzwl.question.ui.myself

import android.view.View
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyMyQuestionBinding
import com.zzwl.question.room.entity.remote.QuestionEntity
import com.zzwl.question.ui.myself.holder.PicHolder

/**
 * Created by qhn on 2018/1/9.
 */

class MyQuestionHolder(val myQuestionEntity: QuestionEntity?) : BaseItem<QuestionItemRcyMyQuestionBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_my_question
    override fun onBind(binding: QuestionItemRcyMyQuestionBinding) {
        super.onBind(binding)
        bindRecycleView(binding)
    }

    private fun bindRecycleView(binding: QuestionItemRcyMyQuestionBinding) {
        val adapter = setupRecyclerView(binding.recyclerView, VirtualLayoutManager.VERTICAL)
        val imgUrl = myQuestionEntity?.imageUrls?.split(",")?.filter { it != "" }
        if (imgUrl?.isEmpty() != false) {
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
        }
        val gridLayoutHelper = GridLayoutHelper(3, imgUrl?.size ?: 0)
        gridLayoutHelper.hGap = 6.dp()
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        imgUrl?.map {
            adapter.renderItems.add(PicHolder(it, spacing = 60))
//        }
        }
    }
}