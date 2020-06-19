package com.g.base.help

import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator

/**
 * Created by G on 2017/9/14 0014.
 */
class ViewSlideBottom {
    companion object {
        // 移出屏幕动画（隐藏动画）
        fun animateOut(view: View) {
            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
            val bottomMargin = layoutParams.bottomMargin
            view.animate().translationY((view.height + bottomMargin).toFloat()).setInterpolator(LinearInterpolator()).start()
        }

        // 移入屏幕动画（显示动画）
        fun animateIn(view: View) {
            view.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
        }
    }
}