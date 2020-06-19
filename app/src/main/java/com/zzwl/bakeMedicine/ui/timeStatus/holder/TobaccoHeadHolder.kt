package com.zzwl.bakeMedicine.ui.timeStatus.holder

import android.databinding.ObservableField
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTobaccoHeadBinding
import com.zzwl.bakeMedicine.room.entity.remote.HouseInfo
import com.g.base.utils.moveToPosition
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel

class TobaccoHeadHolder(private val houseList: ArrayList<HouseInfo>,
                        val selectIdObs: ObservableField<Int?>,
                        val viewModel: TobaccoStatisticsViewModel) : BaseItem<ItemTobaccoHeadBinding>() {
    override val layoutId: Int = R.layout.item_tobacco_head
    override fun onBind(binding: ItemTobaccoHeadBinding) {
        val adapter = setupRecyclerView(binding.recyclerView, VirtualLayoutManager.HORIZONTAL)
        val helper = LinearLayoutHelper(12.dp(), houseList.size)
        adapter.layoutHelpers = listOf(helper as LayoutHelper)
        houseList.forEach { houseInfo ->
            adapter.renderItems.add(PicTextHolder(houseInfo, selectIdObs).apply {
                setOnClickListener {
                    if (selectedIdObs.get() != houseInfo.houseId) {
                        selectedIdObs.set(houseInfo.houseId)
                        viewModel.isRefreshAll = false
                        viewModel.setHouseLiveData(houseInfo.houseId)
                    }
                }
            })
        }

        adapter.notifyDataSetChanged()
        val n = houseList.indexOfFirst { it.houseId == selectIdObs.get()!! }
        moveToPosition(adapter.layoutManager,binding.recyclerView,n)
    }


}