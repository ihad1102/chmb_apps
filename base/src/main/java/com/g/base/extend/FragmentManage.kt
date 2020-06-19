package com.g.base.extend

import android.support.v4.app.FragmentManager

/**
 * Created by G on 2017/9/2 0002.
 */
fun FragmentManager.popTo(index: Int) {
    for (i in index..(this.backStackEntryCount - 1)) {
        this.popBackStack()
    }
}