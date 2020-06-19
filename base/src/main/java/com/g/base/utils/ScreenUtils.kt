package com.g.base.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

fun getScreenWidth(context: Context): Int {
    val windowManager = (context.getSystemService(Context.WINDOW_SERVICE)) as WindowManager
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

fun getScreenHeight(context: Context): Int {
    val windowManager = (context.getSystemService(android.content.Context.WINDOW_SERVICE)) as WindowManager
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.heightPixels
}

