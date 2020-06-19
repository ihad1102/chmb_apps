package com.g.base.extend

import com.g.base.appContent
import java.math.BigDecimal

/**
 * Created by G on 2017/8/12 0012.
 */

fun Number.dp(): Int {
    val scale = appContent.resources.displayMetrics.density
    return (this.toFloat() * scale + 0.5f).toInt()
}

/**
 * 保留几位小数
 */
fun Number.round( scale: Int): String {
    if (scale < 0) {
        throw IllegalArgumentException("The scale must be a positive integer or zero")
    }
    val b = BigDecimal(this.toDouble())
    val one = BigDecimal("1")
    return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString()
}