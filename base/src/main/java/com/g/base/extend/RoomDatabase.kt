package com.g.base.extend

import android.arch.persistence.room.RoomDatabase
import kotlin.concurrent.thread

/**
 * Created by G on 2017/11/20 0020.
 */
fun <T : RoomDatabase> T.runDataBaseTransition(exec: T.() -> Unit) {
    runInTransaction {
        exec()
    }
}