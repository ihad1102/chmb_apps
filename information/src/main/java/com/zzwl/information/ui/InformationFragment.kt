package com.zzwl.information.ui

import android.arch.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.tryCatch
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoViewSwiperefreshRecyclerviewBinding
import com.zzwl.information.router.RouterPage
import com.zzwl.information.ui.holder.InformationHolder
import com.zzwl.information.viewModel.InformationViewModel

class InformationFragment : BaseFragment<InfoViewSwiperefreshRecyclerviewBinding>() {

    override fun setContentView(): Int = R.layout.info_view_swiperefresh_recyclerview
    private val viewModel by lazy { ViewModelProviders.of(this).get(InformationViewModel::class.java) }
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    private var listStatus = ListStatus.Content
    override fun onLazyLoadOnce() {
        initView()
        obsData()
        onReload()
    }

    private fun initView() {
        toolbar.title = "资讯"
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
                viewModel.setInformationPageNum(viewModel.informationList.size / viewModel.limit + 1)
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }

        }

    }

    override fun onReload() {
        viewModel.setInformationPageNum(1)
    }

    private fun obsData() {
        viewModel.obsInformationPage()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.informationList.addAll(it.data!!.list)
                            } else {
                                viewModel.informationList.clear()
                                viewModel.informationList.addAll(it.data!!.list)
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
        val linearLayoutHelper = LinearLayoutHelper(1, viewModel.informationList.size)
        viewModel.informationList.forEach { infoEntity ->
            tryCatch {
                adapter.diffItems.add(InformationHolder(infoEntity.title,
                        infoEntity.source,
                        infoEntity.createTime.substring(0, 16),
                        infoEntity.id,
                        infoEntity.titleImage.url
                ).apply {
                    setOnClickListener {
                        ARouter.getInstance().build(RouterPage.InformationDetailsActivity)
                                .withString("url", infoEntity.content)
                                .withInt("id", infoEntity.id)
                                .withInt("type", infoEntity.contentType)
                                .navigation(context)
                    }
                })
            }
        }
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as InformationHolder).id == (baseItem1 as InformationHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.time == baseItem1.time


                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as InformationHolder).id == (baseItem1 as InformationHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.time == baseItem1.time
                }
        )
    }
}

