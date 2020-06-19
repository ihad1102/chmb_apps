package com.zzwl.question.ui.questionDetail.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyReplyBinding


class ReplyHolder(val headImg: String?,
                  val userName: String?,
                  val content: String?,
                  val replyTime: String?,
                  val isDelete: Boolean = false) : BaseItem<QuestionItemRcyReplyBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_reply
}