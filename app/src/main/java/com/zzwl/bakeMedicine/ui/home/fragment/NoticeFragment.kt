package com.zzwl.bakeMedicine.ui.home.fragment

import android.arch.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.tryCatch
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewSwiperefreshRecyclerviewBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.home.holder.NoticeHolder
import com.zzwl.bakeMedicine.viewModel.NoticeViewModel

@Route(path = RouterPage.NoticeFragment)
class NoticeFragment : BaseFragment<ViewSwiperefreshRecyclerviewBinding>() {

    override fun setContentView(): Int = R.layout.view_swiperefresh_recyclerview
    private val viewModel by lazy { ViewModelProviders.of(this).get(NoticeViewModel::class.java) }
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    private var listStatus = ListStatus.Content
    override fun onLazyLoadOnce() {
        initView()
        obsData()
        onReload()
    }

    override fun onReload() {
        viewModel.setNoticeLiveData(1)
    }

    private fun initView() {

        contentView.refreshLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
            }
        }
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                viewModel.setNoticeLiveData(viewModel.noticeList.size / viewModel.limit + 1)
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }

        }

    }


    private fun obsData() {
        viewModel.obsNotice()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.noticeList.addAll(it.data!!.list)
                            } else {
                                viewModel.noticeList.clear()
                                viewModel.noticeList.addAll(it.data!!.list)
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < viewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            applyData()
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.LoadMore -> {
                                    if (it.error?.code == ErrorCode.EMPTY) {
                                        adapter.setLoadingNoMore()
                                    } else {
                                        adapter.setLoadingFailed()
                                    }
                                }
                                ListStatus.Content -> {
                                    showError(it.error?.message)
                                }
                                ListStatus.Refreshing -> {
                                    contentView.refreshLayout.isRefreshing = false
                                }
                            }
                            listStatus = ListStatus.Content
                        }
                    }


                })
    }

    private fun applyData() {
        val linearLayoutHelper = LinearLayoutHelper(8.dp(), viewModel.noticeList.size)
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        viewModel.noticeList.forEach { it ->
            tryCatch {
                adapter.diffItems.add(NoticeHolder(
                        it.title,
                        it.content,
                        it.createTime.substring(0, 16),
                        it.id
                ))
            }
        }
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as NoticeHolder).id == (baseItem1 as NoticeHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.content == baseItem1.content
                            && baseItem.date == baseItem1.date


                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as NoticeHolder).id == (baseItem1 as NoticeHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.content == baseItem1.content
                            && baseItem.date == baseItem1.date
                }
        )
    }

}