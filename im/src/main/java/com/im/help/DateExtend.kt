package com.im.help

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormatDate(pattern: String): String {
    val format = SimpleDateFormat(pattern, Locale.CHINA)
    return format.format(Date(this)) ?: "??:??"
}