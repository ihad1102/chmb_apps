package com.g.base.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


fun moveToPosition(manager: LinearLayoutManager, mRecyclerView: RecyclerView, n: Int) {
    val firstItem = manager.findFirstVisibleItemPosition()
    val lastItem = manager.findLastVisibleItemPosition()
    when {
        n <= firstItem -> mRecyclerView.scrollToPosition(n)
        n <= lastItem -> {
            val left = mRecyclerView.getChildAt(n - firstItem).left
            mRecyclerView.scrollBy(0, left)
        }
        else -> mRecyclerView.scrollToPosition(n)
    }

}