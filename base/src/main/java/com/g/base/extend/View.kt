package com.g.base.extend

import android.view.View
import android.view.ViewGroup

/**
 * Created by G on 2017/8/15 0015.
 */
fun View.setSpace(height : Number? = null, width : Number? = null){
    val layoutParams = this.layoutParams
    if(height != null)
        layoutParams.height = height.toInt()
    if(width != null)
        layoutParams.width = width.toInt()
    this.layoutParams = layoutParams
}

fun View.setMargin(top: Number? = null, right: Number? = null, bottom: Number? = null, left: Number? = null) {
    val params = layoutParams
    if(params is ViewGroup.MarginLayoutParams ){
        if(top != null){
            params.topMargin = top.toInt()
        }
        if(right != null){
            params.rightMargin = right.toInt()
        }
        if(bottom != null){
            params.bottomMargin = bottom.toInt()
        }
        if(left != null){
            params.leftMargin = left.toInt()
        }
        layoutParams = params
    }
}