package com.zzwl.question.ui.questionDetail.holder

import android.annotation.SuppressLint
import android.databinding.ObservableField
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyCommentBinding
import com.zzwl.question.room.entity.common.Reply
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.question.holder.PicHolder

/**
 * Created by qhn on 2018/1/5.
 */
class CommentHolder(val imageHost: String?,
                    val comment: Reply?,
                    val authorName: String = "",
                    val content: String = "",
                    private val isShowRecycle: Boolean = true,
                    val isShowReply: Boolean = true) : BaseItem<QuestionItemRcyCommentBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_comment
    val obsIsAdopt = ObservableField<Boolean>(comment?.adopt)
    val errorImg = ObservableField(R.drawable.question_ic_default_head_portrait_man)
    override fun onBind(binding: QuestionItemRcyCommentBinding) {
        initView(binding)
        replyList(binding, comment)
    }

    private fun initView(binding: QuestionItemRcyCommentBinding) {
        if (comment?.imageList?.isNotEmpty() == true && isShowRecycle && !comment.del) {
            binding.recyclerView.visibility = View.VISIBLE
            bindRecyclerView(binding)
        } else
            binding.recyclerView.visibility = View.GONE
        when (comment?.like) {
            true -> {
                binding.imgApproval.setImageResource(R.drawable.question_zan_selected)
            }
            false -> {
                binding.imgOppose.setImageResource(R.drawable.question_fandui_selected)
            }

        }
    }

    private fun bindRecyclerView(binding: QuestionItemRcyCommentBinding) {
        val adapter = setupRecyclerView(binding.recyclerView)
        val tempSize = if (comment?.del == true) 0 else comment?.imageList?.size ?: 0
        val gridLayoutHelper = GridLayoutHelper(3, tempSize)
        gridLayoutHelper.hGap = 6.dp()
        gridLayoutHelper.setPadding(6.dp(), 0, 6.dp(), 0)
        gridLayoutHelper.aspectRatio = 3.1f
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = listOf(gridLayoutHelper as LayoutHelper)
        if (comment?.del == true) {
            return
        }
        comment?.imageList?.forEachIndexed { index, imgUrl ->
            adapter.renderItems.add(PicHolder(imgUrl?.url, clickable = true)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance().build(RouterPage.ImagePreviewActivity)
                                    .withInt("index", index)
                                    .withString("image", comment.imageList?.map { it?.url }?.joinToString(",")).navigation(binding.root.context)
                        }
                    })
        }
    }

    //回复列表
    @SuppressLint("SetTextI18n")
    private fun replyList(binding: QuestionItemRcyCommentBinding, comment: Reply?) {
        when {
            comment?.comments?.size == 0 || !isShowReply -> {
                binding.tvReply1.visibility = View.GONE
                binding.tvReply2.visibility = View.GONE
                binding.tvReply3.visibility = View.GONE
            }
            comment?.comments?.size == 1 -> {
                binding.tvReply1.visibility = View.VISIBLE
                binding.tvReply2.visibility = View.GONE
                binding.tvReply3.visibility = View.GONE

                val tempContent = if (comment.comments!![0]!!.del) {
                    binding.tvReply1.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textColorWeek))
                    "该回复不存在或已删除"
                } else comment.comments!![0]?.content
                val span = SpannableString(comment.comments!![0]?.user?.realName + ":" + tempContent)
                span.setSpan(ForegroundColorSpan(Color.parseColor("#69bf85")), 0, (comment.comments!![0]?.user?.realName?.length
                        ?: 0) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.tvReply1.text = span
            }
            comment?.comments?.size == 2 -> {
                binding.tvReply1.visibility = View.VISIBLE
                binding.tvReply2.visibility = View.VISIBLE
                binding.tvReply3.visibility = View.GONE


                val tempContent1 =
                        if (comment.comments!![0]!!.del) {
                            binding.tvReply1.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textColorWeek))
                            "该回复不存在或已删除"
                        } else comment.comments!![0]?.content
                val tempContent2 =
                        if (comment.comments!![1]!!.del) {
                            binding.tvReply2.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textColorWeek))
                            "该回复不存在或已删除"
                        } else comment.comments!![1]?.content


                val span = SpannableString(comment.comments!![0]?.user?.realName + ":" + tempContent1)
                span.setSpan(ForegroundColorSpan(Color.parseColor("#69bf85")), 0, (comment.comments!![0]?.user?.realName?.length
                        ?: 0) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val span1 = SpannableString(comment.comments!![1]?.user?.realName + ":" + tempContent2)
                span1.setSpan(ForegroundColorSpan(Color.parseColor("#69bf85")), 0, (comment.comments!![1]?.user?.realName?.length
                        ?: 0) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.tvReply1.text = span
                binding.tvReply2.text = span1
            }
            comment?.comments?.size ?: 0 >= 3 -> {
                binding.tvReply1.visibility = View.VISIBLE
                binding.tvReply2.visibility = View.VISIBLE
                binding.tvReply3.visibility = View.VISIBLE


                val tempContent1 = if (comment?.comments!![0]!!.del) {
                    binding.tvReply1.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textColorWeek))
                    "该回复不存在或已删除"
                } else comment.comments!![0]?.content

                val tempContent2 = if (comment.comments!![1]!!.del) {
                    binding.tvReply2.setTextColor(ContextCompat.getColor(binding.root.context, R.color.textColorWeek))
                    "该回复不存在或已删除"
                } else comment.comments!![1]?.content


                val span = SpannableString(comment.comments!![0]?.user?.realName + ":" + tempContent1)
                span.setSpan(ForegroundColorSpan(Color.parseColor("#026A52")), 0, (comment.comments!![0]?.user?.realName?.length
                        ?: 0) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val span1 = SpannableString(comment.comments!![1]?.user?.realName + ":" + tempContent2)
                span1.setSpan(ForegroundColorSpan(Color.parseColor("#026A52")), 0, (comment.comments!![1]?.user?.realName?.length
                        ?: 0) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.tvReply1.text = span
                binding.tvReply2.text = span1
                binding.tvReply3.text = "剩余${comment.comments!!.size - 2}条回复 >"
            }

        }
    }

}