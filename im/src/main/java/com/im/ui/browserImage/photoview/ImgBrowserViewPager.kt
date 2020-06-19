package com.im.ui.browserImage.photoview


import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class ImgBrowserViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }
}
