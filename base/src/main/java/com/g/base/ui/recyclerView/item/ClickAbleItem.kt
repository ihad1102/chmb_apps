package com.g.base.ui.recyclerView.item

import android.view.View

/**
 * Created by G on 2017/11/24 0024.
 */
open class ClickAbleItem {
    //点击处理
    protected var onClickListener: ((view: View, ext: Any?) -> Unit)? = null

    //不带额外参数的点击回调
    open fun setOnClickListener(call: (view: View) -> Unit) {
        onClickListener = fun(view, _) {
            call(view)
        }
    }

    //点击事件处理携带额外参数
    open fun setOnClickExtListener(call: (view: View, ext: Any?) -> Unit) {
        onClickListener = fun(view, ext) {
            call(view, ext)
        }
    }

    //视图点击回调
    open fun onClick(view: View) {
        onClickListener?.invoke(view, this)
    }
}