package com.g.base.ui.widget.swipeback

import android.content.Context
import android.graphics.Canvas
import android.view.View

internal class PreviousPageView(context: Context) : View(context){
    private var mView: View? = null

    fun cacheView(view: View) {
        mView = view
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (mView != null) {
            mView!!.draw(canvas)
            mView = null
        }
    }
}

