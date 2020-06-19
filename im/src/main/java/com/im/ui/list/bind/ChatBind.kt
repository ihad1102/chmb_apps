package com.im.ui.list.bind

import android.view.View
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.model.Conversation
import com.g.base.ui.recyclerView.item.BaseItem
import com.im.R
import com.im.databinding.ItemChatBinding
import com.im.help.toFormatDate

class ChatBind(var conversation: Conversation) : BaseItem<ItemChatBinding>() {
    override val layoutId: Int = R.layout.item_chat
    val placeholder = R.drawable.ic_default_head
    var lasMessageDate = conversation.lastMsgDate
    lateinit var headerImage: String
    lateinit var title: String
    lateinit var lastMessage: String
    lateinit var lastTime: String
    lateinit var unread: String

    override fun onBind(binding: ItemChatBinding) {
        headerImage = conversation.avatarFile?.path ?: ""
        title = conversation.title
        lastMessage = getMessageText()
        lastTime = getMessageTime()
        unread = getUnRead()
    }

    override fun onClick(view: View) {
        onClickListener?.invoke(view, 0)
    }

    fun onLongClick(view: View): Boolean {
        onClickListener?.invoke(view, 1)
        return true
    }

    private fun getUnRead(): String {
        val unReadMsgCnt = conversation.unReadMsgCnt
        return if (unReadMsgCnt > 0)
            if (unReadMsgCnt > 99)
                "99+"
            else
                unReadMsgCnt.toString()
        else
            ""
    }

    private fun getMessageTime(): String {
        val lastMsgDate = conversation.lastMsgDate ?: System.currentTimeMillis()
        return if (System.currentTimeMillis() - lastMsgDate < 1000 * 60 * 60 * 24)
            lastMsgDate.toFormatDate("HH:mm")
        else
            "昨天"
    }

    private fun getMessageText(): String {
        return when (conversation.latestType) {
            ContentType.text -> conversation.latestText ?: ""
            ContentType.image -> "[图片消息]"
            ContentType.video -> "[视频消息]"
            ContentType.voice -> "[语音消息]"
            ContentType.custom -> "[商品消息]"
            else -> "[未知消息]"
        }
    }
}