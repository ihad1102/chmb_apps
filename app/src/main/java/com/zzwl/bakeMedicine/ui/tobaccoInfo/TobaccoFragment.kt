package com.zzwl.bakeMedicine.ui.tobaccoInfo

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewRecyclerviewBinding
import com.zzwl.bakeMedicine.event.RefreshTobaccoSelectedEvent
import com.zzwl.bakeMedicine.ui.timeStatus.holder.PicTextHolder
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel
import org.greenrobot.eventbus.EventBus

/**
 *type,1,2,0 : 运行,故障,错误
 */

class TobaccoFragment : BaseFragment<ViewRecyclerviewBinding>() {
    override fun setContentView(): Int = R.layout.view_recyclerview
    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TobaccoStatisticsViewModel::class.java) }
    override fun onLazyLoadOnce() {
        super.onLazyLoadOnce()
        showContentView()
        initView(arguments!!.getInt("type"))
    }

    private fun initView(type: Int) {
        val data = viewModel.tobaccoListInfoEntity.houseInfoList.filter { it.workStatus == type }

        val selectedIdObs = ObservableField(arguments!!.getInt("selectedId", 0))
        data.forEach {
            adapter.renderItems.add(PicTextHolder(it, selectedIdObs).apply {
                setOnClickListener {
                    if (arguments!!.getBoolean("isClickAble", true))
//                        if (selectedIdObs.get() != houseInfo.houseId) {//去掉重复点击保护
                            selectedIdObs.set(houseInfo.houseId)
                            activity!!.finish()
                            EventBus.getDefault().post(RefreshTobaccoSelectedEvent(true, houseInfo.houseId))
//                        }
                }
            })
        }
        val gridLayoutHelper = GridLayoutHelper(4, data.size)
        gridLayoutHelper.hGap = 6.dp()
        gridLayoutHelper.setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = listOf(gridLayoutHelper as LayoutHelper)
        adapter.notifyDataSetChanged()
    }
}