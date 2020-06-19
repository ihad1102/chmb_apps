package com.zzwl.bakeMedicine.weidget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by G on 2017/11/13 0013.
 */
class DisScrollViewPager : ViewPager{
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean = false
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
}