package com.g.base.extend

import java.util.regex.Pattern

fun String.getSrc(): String {
    val p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>")//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
    val m = p.matcher(this)
    return if (m.find())
        m.group(1)
    else
        ""

}