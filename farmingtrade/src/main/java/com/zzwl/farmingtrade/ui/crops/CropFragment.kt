package com.zzwl.farmingtrade.ui.crops

import android.arch.lifecycle.ViewModelProviders
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.toast
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingViewRecyclerviewBinding
import com.zzwl.farmingtrade.event.CropReloadEvent
import com.zzwl.farmingtrade.event.SelectedCropsEvent
import com.zzwl.farmingtrade.room.entity.remote.FollowedCropEntity
import com.zzwl.farmingtrade.ui.crops.holder.CropHolder
import com.zzwl.farmingtrade.viewModel.CropViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by qhn on 2018/1/8.
 */
class CropFragment : BaseFragment<FarmingViewRecyclerviewBinding>() {
    var isFollowed: Boolean? = false
    var isOnlyClick: Boolean? = false
    var selectedId: Int? = null
    var amount: Int = 0
    override fun setContentView(): Int = R.layout.farming_view_recyclerview

    override fun onLazyLoadOnce() {
        isFollowed = arguments?.getBoolean("isFollowed", false)
        isOnlyClick = arguments?.getBoolean("isOnlyClick", false)
        selectedId = arguments?.getInt("selectedId", 0)
        amount = arguments?.getInt("amount", 0) ?: 0
        initView()
        onReload()
    }

    private fun initView() {
        contentView.refreshLayout.setOnRefreshListener({
            onReload()
        })

    }

    override fun onReload() {
        requestCrops(ViewModelProviders.of(activity!!)
                .get(CropViewModel::class.java), arguments?.getInt("categoryId", 0) ?: 0)
    }

    private fun requestCrops(viewModel: CropViewModel, categoryId: Int) {
        viewModel.getCrop(categoryId, isFollowed ?: true).observeEx(this, {
            when (it.status) {
                Status.Loading -> {
                    showLoading()
                }
                Status.Content -> {
                    bindRecycleView(it.data!!)
                    showContentView()
                    if (contentView.refreshLayout.isRefreshing)
                        contentView.refreshLayout.isRefreshing = false
                }
                Status.Error -> {
                    showError(it.error?.message)
                }
            }

        })
    }

    private fun bindRecycleView(list: List<FollowedCropEntity?>) {
        val adapter = setupRecyclerView(contentView.recyclerView)
        val gridLayoutHelper = GridLayoutHelper(3, list.size)
        gridLayoutHelper.vGap = 4.dp()
        gridLayoutHelper.paddingTop = 8.dp()
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = listOf(gridLayoutHelper)
        list.forEach {
            adapter.renderItems.add(CropHolder(it,
                    isFollowed ?: false, selectedId)
                    .apply {
                        setOnClickListener {
                            if (imgSelect.get() == true) {
                                imgSelect.set(false)
                                EventBus.getDefault().post(SelectedCropsEvent(cropEntity?.id, 0, cropEntity?.name
                                        ?: ""))
                            } else {
                                if ((activity as CropsActivity).addCropList.size > amount && !isFollowed) {
                                    (activity as CropsActivity).toast("只能选${amount + 1}种作物")
                                    return@setOnClickListener
                                }
//                                if (!isFollowed && isOnlyClick == true) {
//                                    ARouter.getInstance().build(RouterPage.FertilizerAndSeedListActivity)
//                                            .withInt("type", 2)
//                                            .withInt("id", cropEntity?.id ?: 0)
//                                            .navigation()
//
//                                    return@setOnClickListener
//                                }
                                imgSelect.set(true)
                                EventBus.getDefault().post(SelectedCropsEvent(cropEntity?.id, 1, cropEntity?.name
                                        ?: ""))
                            }
                        }
                    })

        }
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(cropReloadEvent: CropReloadEvent) {
        if (cropReloadEvent.isReload)
            onReload()
    }
}