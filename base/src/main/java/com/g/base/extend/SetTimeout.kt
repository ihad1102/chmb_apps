package com.g.base.extend

import android.os.Handler
import com.g.base.appContent

/**
 * Created by G on 2017/8/12 0012.
 */

fun setTimeout(timeOut: Long = 1000L, exec: () -> Unit): () -> Unit {
    val handler = Handler(appContent.mainLooper)
    val runnable = Runnable {
        exec.invoke()
    }
    handler.postDelayed(runnable, Math.max(timeOut, 0L))
    return {
        handler.removeCallbacks(runnable)
    }
}