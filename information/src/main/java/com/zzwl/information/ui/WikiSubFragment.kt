package com.zzwl.information.ui

import android.arch.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.api.ErrorCode
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoViewSwiperefreshRecyclerviewBinding
import com.zzwl.information.ui.holder.WikiHolder
import com.zzwl.information.router.RouterPage
import com.zzwl.information.viewModel.WikiViewModel

class WikiSubFragment : BaseFragment<InfoViewSwiperefreshRecyclerviewBinding>() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(WikiViewModel::class.java) }
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    private val type by lazy { arguments!!.getInt("type", -1) }
    private var listStatus = ListStatus.Content
    override fun setContentView(): Int = R.layout.info_view_swiperefresh_recyclerview
    override fun onLazyLoadOnce() {
        initView()
        obsData()
        onReload()
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
                viewModel.operateWikiList(viewModel.wikiEntityList.size / viewModel.limit + 1, type)
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }

        }

    }

    private fun obsData() {
        viewModel.obsWikiList()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.wikiEntityList.addAll(it.data!!)
                            } else {
                                viewModel.wikiEntityList.clear()
                                viewModel.wikiEntityList.addAll(it.data!!)
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.size < viewModel.limit)
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

        viewModel.wikiEntityList.forEach { it ->
            adapter.diffItems.add(WikiHolder(
                    it.title,
                    it.intro,
                    it.imageUrl,
                    it.id
            ).apply {
                setOnClickListener {
                    ARouter.getInstance().build(RouterPage.WikiDetailsActivity).withInt("id", id).navigation(context)
                }
            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as WikiHolder).id == (baseItem1 as WikiHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.intro == baseItem1.intro
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as WikiHolder).id == (baseItem1 as WikiHolder).id
                            && baseItem.title == baseItem1.title
                            && baseItem.intro == baseItem1.intro
                }
        )
    }


    override fun onReload() {
        viewModel.operateWikiList(1, type)
    }
}