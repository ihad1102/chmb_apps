package com.zzwl.question.ui.myself.holder

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyMyAnswerBinding
import com.zzwl.question.room.entity.remote.MyAnswerREntity


/**
 * Created by qhn on 2018/1/9.
 */
class MyAnswerHolder(val answerEntity: MyAnswerREntity?) : BaseItem<QuestionItemRcyMyAnswerBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_my_answer

    private val span: Spannable = SpannableString("回答${answerEntity?.question?.postUser?.userName}的问题:  ${answerEntity?.content}")
    override fun onBind(binding: QuestionItemRcyMyAnswerBinding) {
        initView(binding)
        bindCommentRecycleView(binding)
        bindContentRecycleView(binding)

    }

    @SuppressLint("SetTextI18n")
    private fun initView(binding: QuestionItemRcyMyAnswerBinding) {
        span.setSpan(ForegroundColorSpan(Color.parseColor("#69bf85")), 2, (answerEntity?.question?.postUser?.userName?.length
                ?: 0) + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvComment.text = span
        binding.tvAuthorName.text = "@${answerEntity?.question?.postUser?.userName}"
    }

    //评论的内容
    private fun bindCommentRecycleView(binding: QuestionItemRcyMyAnswerBinding) {
        val commentAdapter = setupRecyclerView(binding.rcyComment, VirtualLayoutManager.VERTICAL)

        if ((answerEntity?.imageList?.size ?: 0) > 0) {
            binding.rcyComment.visibility = View.VISIBLE
        } else {
            binding.rcyComment.visibility = View.GONE
        }
        val gridLayoutHelper = GridLayoutHelper(3, answerEntity?.imageList?.size ?: 0)
        gridLayoutHelper.hGap = 4.dp()
//        gridLayoutHelper.setPadding(8.dp(),0,8.dp(),0)
        gridLayoutHelper.setAutoExpand(false)
        commentAdapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        answerEntity?.imageList?.map { commentAdapter.renderItems.add(PicHolder(it?.url, spacing = 56)) }
    }

    //被评论的问题
    private fun bindContentRecycleView(binding: QuestionItemRcyMyAnswerBinding) {
        val contentAdapter = setupRecyclerView(binding.rcyContent, VirtualLayoutManager.VERTICAL)
        if ((answerEntity?.question?.imageUrlList?.size ?: 0) > 0) {
            binding.rcyContent.visibility = View.VISIBLE
        } else binding.rcyContent.visibility = View.GONE
        val gridLayoutHelper = GridLayoutHelper(3, answerEntity?.question?.imageUrlList?.size ?: 0)
        gridLayoutHelper.hGap = 4.dp()
        gridLayoutHelper.setAutoExpand(false)
        gridLayoutHelper.setPadding(8.dp(), 0, 8.dp(), 0)
        gridLayoutHelper.bgColor = Color.parseColor("#f5f5f5")
        contentAdapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        answerEntity?.question?.imageUrlList?.map {
            contentAdapter.renderItems.add(PicHolder(it, spacing = 72))
        }
    }


}