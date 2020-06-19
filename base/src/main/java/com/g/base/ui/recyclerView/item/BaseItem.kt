package com.g.base.ui.recyclerView.item

import android.databinding.ViewDataBinding
import com.g.base.BR

/**
 * Created by G on 2017/11/24 0024.
 */
abstract class BaseItem<in T : ViewDataBinding> : ClickAbleItem() {
    abstract val layoutId: Int
    open val variableId: Int = BR.data
    open val recyclerAble: Boolean = true
    var tempOnceRecyclerAble = false

    open fun onBind(binding: T) {}
}