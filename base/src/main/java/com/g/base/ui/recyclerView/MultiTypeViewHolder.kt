package com.g.base.ui.recyclerView

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.g.base.ui.recyclerView.item.BaseItem

/**
 * Created by G on 2017/11/24 0024.
 */
class MultiTypeViewHolder<out T : ViewDataBinding>(private val binding: T) : RecyclerView.ViewHolder(binding.root) {
    private var hasLoad = false
    fun bindTo(item: BaseItem<T>) {
        if (!item.recyclerAble && !item.tempOnceRecyclerAble && hasLoad) return
        item.tempOnceRecyclerAble = false
        hasLoad = true
        binding.setVariable(item.variableId, item)
        item.onBind(binding)
        binding.executePendingBindings()
    }
}