package com.g.base.ui.recyclerView

import android.support.v7.widget.RecyclerView
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.g.base.appContent

/**
 * Created by G on 2017/11/24 0024.
 */
fun setupRecyclerView(recyclerView: RecyclerView, orientation: Int = VirtualLayoutManager.VERTICAL, reverseLayout: Boolean = false): MultiTypeAdapter {
    val virtualLayoutManager = VirtualLayoutManager(appContent, orientation, reverseLayout)
    val multiTypeAdapter = MultiTypeAdapter(virtualLayoutManager)
    recyclerView.adapter = multiTypeAdapter
    recyclerView.layoutManager = virtualLayoutManager
    return multiTypeAdapter
}