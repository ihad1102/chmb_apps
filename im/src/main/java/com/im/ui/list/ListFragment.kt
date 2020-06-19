package com.im.ui.list

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.observeNullableEx
import com.g.base.extend.observeNullableExOnce
import com.g.base.extend.toast
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.ListStatus
import com.g.base.token.TokenManage
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.im.Im
import com.im.ImMessageRegister
import com.im.R
import com.im.databinding.ActivityListBinding
import com.im.router.ImRouter
import com.im.ui.list.bind.ChatBind

class ListFragment : BaseFragment<ActivityListBinding>() {
    override fun setContentView(): Int = R.layout.activity_list

    private val adapter by lazy { setupRecyclerView(contentView.recycler) }
    private var listStatus = ListStatus.Content
    private val messageRegister = ImMessageRegister()
    private var isFirstInit = false

    override fun onReload() {
        listStatus = ListStatus.Content
        operateData()
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null) showNeedOauth()
    }

    override fun onLazyLoadOnce() {
        setView()
        Im.login(TokenManage.getToken())
        Im.loginStatus.observeNullableEx(this) {
            when (it) {
                0 -> showLoading()
                1 -> operateData()
                2 -> showNeedOauth()
            }
        }
        Im.loginStatus.observeNullableExOnce {
            if (it == 1) {
                messageRegister.observeNullableEx(this) {
                    operateData()
                }
            }
        }
    }

    override fun onLazyLoad() {
        if (!isFirstInit) return
        operateData()
    }

    private fun operateData() {
        isFirstInit = true
        val size = JMessageClient.getConversationList()?.size ?: 0
        if (size == 0)
            showError("暂时没有聊天消息", R.drawable.err_empty)
        else
            applyData()
    }

    private fun setView() {
        contentView.pullRefresh.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
            }
        }
    }

    private fun applyData() {
        val data = JMessageClient.getConversationList()

        adapter.layoutHelpers = listOf(LinearLayoutHelper(2, data.size))

        data.forEach { conversation ->
            adapter.diffItems.add(ChatBind(conversation).apply {
                setOnClickExtListener { _, ext ->
                    val i = ext as Int
                    if (i == 0) {
                        val userInfo = conversation.targetInfo as UserInfo
                        ARouter.getInstance().build(ImRouter.ChatActivity).withString("targetName", userInfo.userName).navigation(activity!!)
                    } else {
                        buildDialog(conversation)
                    }
                }
            })
        }

        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as ChatBind).conversation.id ==
                            (baseItem1 as ChatBind).conversation.id
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as ChatBind).conversation.id == (baseItem1 as ChatBind).conversation.id &&
                            baseItem.lasMessageDate == baseItem1.lasMessageDate
                },
                {
                    contentView.pullRefresh.isRefreshing = false
                    listStatus = ListStatus.Content
                    showContentView()
                })
    }

    private fun buildDialog(conversation: Conversation) {
        val items = ArrayList<CharSequence>()
        items.add("标记为已读")
        items.add("删除消息")

        AlertDialog.Builder(activity!!)
                .apply {
                    setCancelable(true)
                    setItems(items.toTypedArray()) { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                        val userInfo = conversation.targetInfo as UserInfo
                        if (i == 0) {
                            conversation.resetUnreadCount()
                        } else {
                            JMessageClient.deleteSingleConversation(userInfo.userName, Im.KEY)
                            val indexOfFirst = adapter.renderItems.indexOfFirst { (it as ChatBind).conversation === conversation }
                            adapter.renderItems.removeAt(indexOfFirst)
                            adapter.notifyItemRemoved(indexOfFirst)
                        }
                    }
                    show()
                }
    }
}