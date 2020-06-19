package com.zzwl.question.ui.message

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.api.ErrorCode
import com.g.base.extend.*
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionViewSwipeRecyclerBinding
import com.zzwl.question.model.bindView.AvatarTitleTipBadgeBind
import com.zzwl.question.other.navHelp.navHandle
import com.zzwl.question.room.entity.remote.MessageREntity
import com.zzwl.question.router.RouterPage
import com.zzwl.question.viewModel.MessageViewModel

/**
 * Created by G on 2017/11/25 0025.
 */
@Route(path = RouterPage.MessageListFragment)
class MessageListFragment : BaseFragment<QuestionViewSwipeRecyclerBinding>() {
    override fun setContentView(): Int = R.layout.question_view_swipe_recycler
    private val messageViewModel by lazy { ViewModelProviders.of(this).get(MessageViewModel::class.java) }

    private var listStatus = ListStatus.Content
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }

    @JvmField
    @Autowired
    var type = ""

    override fun onLazyLoadOnce() {
        super.onLazyLoadOnce()
        ARouter.getInstance().inject(this)

        messageViewModel.operateMessageList(type)

        contentView.swipeLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                messageViewModel.operateMessageList(type)
                listStatus = ListStatus.Refreshing
            } else {
                contentView.swipeLayout.isRefreshing = false
                toast("正在执行刷新操作请稍后再试")
            }
        }

        obsData()
    }

    override fun onReload() {
        messageViewModel.operateMessageList(type)
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null)
            showNeedOauth()
        else
            messageViewModel.operateMessageList(type)
    }

    private fun obsData() {
        messageViewModel.obsMessageList()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            listStatus = ListStatus.Content
                            applyData(it.data!!)
                            contentView.swipeLayout.isRefreshing = false
                            showContentView()
                        }
                        Status.Error -> {
                            listStatus = ListStatus.Content
                            if (it.error?.code == ErrorCode.EMPTY)
                                showError("暂时还没有相关的消息哦~", R.drawable.err_empty)
                            else
                                showError(it.error?.message)
                        }
                    }
                }
    }

    private fun applyData(data: List<MessageREntity>) {
        adapter.diffItems.addAll(data.map {
            1L.date()
            val tempAvator: Any = if (it.avatar.isEmpty()) R.drawable.question_ic_msg else it.avatar
            AvatarTitleTipBadgeBind(tempAvator, it.title, it.subtitle, it.date, it.id, if (it.read == 1) 0 else 1)
                    .apply {
                        setOnClickExtListener { _, ext ->
                            if (ext as Boolean) {
//                                showAlertDialog(it.read == 0, it.id)
                            } else {
                                messageViewModel.markMessageRead(it.id, type).observeExOnce(this@MessageListFragment) {}
                                navHandle(it.navParams)
                            }
                        }
                    }
        })
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as AvatarTitleTipBadgeBind).title.get() == (baseItem1 as AvatarTitleTipBadgeBind).title.get()
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as AvatarTitleTipBadgeBind).tip.get() == (baseItem1 as AvatarTitleTipBadgeBind).tip.get() &&
                            baseItem.subtitle.get() == baseItem1.subtitle.get() &&
                            baseItem.badge.get() == baseItem1.badge.get()
                })
    }

    private fun showAlertDialog(markRead: Boolean, id: String) {
        val items = ArrayList<CharSequence>()

        items.add("标记全部消息为已读")
        items.add("标记为已读")
        items.add("删除消息")

        AlertDialog.Builder(activity as FragmentActivity)
                .apply {
                    setCancelable(true)
                    setItems(items.toTypedArray()) { dialog, which ->
                        dialog.dismiss()
                        when (which) {
                            0 -> {
                                messageViewModel.markAllMessageRead(type)
                                        .progressDialog(onError = { it.dismiss() })
                                        .observeExOnce(this@MessageListFragment) {}
                            }
                            1 -> {
                                if (markRead) {
                                    messageViewModel.markMessageRead(id, type)
                                            .progressDialog(onError = { it.dismiss() })
                                            .observeExOnce(this@MessageListFragment) {}
                                } else {
                                    toast("该消息已经是已读状态")
                                }
                            }
                            2 -> messageViewModel.delMessage(id, type)
                                    .progressDialog(onError = { it.dismiss() })
                                    .observeExOnce(this@MessageListFragment) {}
                        }
                    }
                    show()
                }
    }
}