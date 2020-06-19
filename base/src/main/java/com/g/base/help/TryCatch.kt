package com.g.base.help

import java.lang.Exception

/**
 * Created by G on 2017/11/7 0007.
 */
fun tryCatch(log: Boolean = false, exec: () -> Unit) {
    try {
        exec.invoke()
    } catch (error: Exception) {
        if (log)
            e(error.message)
    }
}