package com.g.base.utils

import android.view.View
import android.widget.PopupWindow

/**
 *  Gravity.CENTER：在showAsDropDown()中是跟 Gravity.LEFT一样，在showAtLocation()中Gravity.CENTER才有效果
 */
object PopupWindowUtils {
    fun build(view: View,
              anchorView: View,
              width: Int,
              height: Int,
              xOff: Int,
              yOff: Int,
              gravity: Int): PopupWindow {
        val popupWindow = PopupWindow(view, width, height)
        popupWindow.setBackgroundDrawable(null)
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchorView, xOff, yOff, gravity)
        return popupWindow
    }
}