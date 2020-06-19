package com.zzwl.question.ui.expert.fragment

import android.arch.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionViewSwiperefreshRecyclerviewBinding
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.expert.holder.ExpertHolder
import com.zzwl.question.viewModel.MyFollowViewModel

/**
 * Created by qhn on 2018/1/11.
 * 我的关注-专家
 */
class ExpertFragment : BaseFragment<QuestionViewSwiperefreshRecyclerviewBinding>() {
    override fun setContentView(): Int = R.layout.question_view_swiperefresh_recyclerview
    lateinit var viewModel: MyFollowViewModel
    private var listStatus: ListStatus = ListStatus.Content
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    override fun onLazyLoadOnce() {
        initView()
        viewModel = ViewModelProviders.of(activity!!).get(MyFollowViewModel::class.java)
        obsExpert()
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
                viewModel.setObsExpertLiveData(viewModel.expertList.size / viewModel.expertLimit + 1)
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }

        }

    }

    override fun onReload() {
        viewModel.setObsExpertLiveData(1)
    }

    private fun obsExpert() {
        viewModel.obsMyFollowExpert().resultNotNull().observeEx(this, {
            when (it.status) {
                Status.Loading -> {
                    if (listStatus == ListStatus.Content)
                        showLoading()
                }
                Status.Content -> {
                    if (listStatus == ListStatus.LoadMore) {
                        viewModel.expertList.addAll(it.data!!)
                    } else {
                        viewModel.expertList.clear()
                        viewModel.expertList.addAll(it.data!!)
                        if (contentView.refreshLayout.isRefreshing)
                            contentView.refreshLayout.isRefreshing = false
                    }
                    if (it.data!!.size < viewModel.expertLimit)
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
                            showLoading()
                        }
                        ListStatus.Content -> {
                            showError(it.error?.message)
                        }
                        ListStatus.Refreshing -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                        }
                    }
                    listStatus = ListStatus.Content
                }
            }

        })
    }

    private fun applyData() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.expertList.size
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        linearLayoutHelper.setDividerHeight(12.dp())
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        adapter.diffItems.addAll(viewModel.expertList.map {
            ExpertHolder(it, false).apply {
                setOnClickListener {
                    ARouter.getInstance().build(RouterPage.ExpertInformationActivity).withString("id", expert?.id).navigation()
                }
            }
        })
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as ExpertHolder).expert?.id == (baseItem1 as ExpertHolder).expert?.id
                            && baseItem.expert?.intro == baseItem1.expert?.intro
                            && baseItem.expert?.headImg?.url == baseItem1.expert?.headImg?.url
                            && baseItem.expert?.userName == baseItem1.expert?.userName


                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as ExpertHolder).expert?.id == (baseItem1 as ExpertHolder).expert?.id
                            && baseItem.expert?.intro == baseItem1.expert?.intro
                            && baseItem.expert?.headImg?.url == baseItem1.expert?.headImg?.url
                            && baseItem.expert?.userName == baseItem1.expert?.userName
                }
        )

    }


}