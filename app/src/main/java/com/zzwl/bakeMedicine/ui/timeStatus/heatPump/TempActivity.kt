package com.zzwl.bakeMedicine.ui.timeStatus.heatPump

import android.databinding.ObservableField
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewRecyclerviewBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder.TempHolder

@Route(path = RouterPage.TempActivity)
class TempActivity : BaseActivity<ViewRecyclerviewBinding>() {
    override var hasToolbar: Boolean = true

    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recyclerview)
        showContentView()
        initView()
        initRecyclerView()
    }

    private fun initView() {
        toolbar.title = "温度状态"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initRecyclerView() {
        val list = arrayListOf("1#排气温度", "1#吸气温度", "1#盘管温度", "2#排气温度",
                "2#吸气温度", "2#盘管温度", "3#排气温度", "3#吸气温度","3#吸气温度")
        val gridLayoutHelper = GridLayoutHelper(3, list.size)
        gridLayoutHelper.setAutoExpand(false)
        gridLayoutHelper.vGap = 8.dp()
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        list.forEachIndexed { _, s ->
            adapter.renderItems.add(TempHolder(ObservableField(s)))
        }
        adapter.notifyDataSetChanged()
    }

}