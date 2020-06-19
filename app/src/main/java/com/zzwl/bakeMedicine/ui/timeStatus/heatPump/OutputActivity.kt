package com.zzwl.bakeMedicine.ui.timeStatus.heatPump

import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewSwiperefreshRecyclerviewBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder.OutputHolder

@Route(path = RouterPage.OutputActivity)
class OutputActivity : BaseActivity<ViewSwiperefreshRecyclerviewBinding>() {
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_swiperefresh_recyclerview)
        showContentView()
        onReload()
        initView()
        initRecyclerView()

    }

    private fun initRecyclerView() {
        val list = arrayListOf("循环风机正转", "循环风机反转", "进风风机", "出风风机",
                "辅助加热", "排风风机", "1#压缩机", "1#回气加热", "2#压缩机", "2#回气加热",
                "3#压缩机", "3#回气加热", "排湿风机", "故障指示")
        val gridLayoutHelper = GridLayoutHelper(2, 14)
        gridLayoutHelper.setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
        gridLayoutHelper.setAutoExpand(false)
        gridLayoutHelper.hGap = 2.dp()
        gridLayoutHelper.vGap = 8.dp()
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        list.forEachIndexed { index, s ->
            adapter.renderItems.add(OutputHolder(ObservableField(s), ObservableField((index != 2 && index != 6 && index != 7 && index != 12 && index != 13))))
        }
        adapter.notifyDataSetChanged()
    }

    private fun initView() {
        toolbar.title = "输出状态"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        contentView.refreshLayout.setOnRefreshListener {
            if (contentView.refreshLayout.isRefreshing)
                contentView.refreshLayout.isRefreshing = false
        }

    }

    override fun onReload() {

    }


}