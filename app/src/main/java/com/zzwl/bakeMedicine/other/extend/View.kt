package com.zzwl.bakeMedicine.other.extend

import android.graphics.Paint

 fun Paint.getTextWidth(str: String ): Int {
    var iRet = 0
    if (str.isNotEmpty()) {
        val len = str.length
        val widths = FloatArray(len)
        this.getTextWidths(str, widths)
        for (j in 0 until len) {
            iRet += Math.ceil(widths[j].toDouble()).toInt()
        }
    }
    return iRet
}