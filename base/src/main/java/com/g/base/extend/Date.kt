package com.g.base.extend

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by G on 2018/1/17 0017.
 */
fun Long.date(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    return format.format(Date(this))
}

fun String.date() : String{
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    return format.format(Date(this))
}