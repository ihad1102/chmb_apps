package com.zzwl.question.model.bindView

import android.databinding.ObservableField
import android.view.View
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemAvatarTitleSubTipBadgeBinding

/**
 * Created by G on 2017/11/26 0026.
 */
class AvatarTitleTipBadgeBind(avatar: Any = R.drawable.question_ic_msg, title: String = "", subtitle: String = "", tip: String = "", val id: String = "", badge: Int = 0, clickable: Boolean = true) : BaseItem<QuestionItemAvatarTitleSubTipBadgeBinding>() {
    override val layoutId: Int = R.layout.question_item_avatar_title_sub_tip_badge

    val avatar = ObservableField(avatar)
    val title = ObservableField(title)
    val subtitle = ObservableField(subtitle)
    val tip = ObservableField(tip)
    val badge = ObservableField(badge)
    val clickable = ObservableField(clickable)

    override fun onClick(view: View) {
        onClickListener?.invoke(view, false)
    }

    fun onLongClick(view: View): Boolean {
        onClickListener?.invoke(view, true)
        return true
    }
}