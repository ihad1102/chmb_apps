package com.g.base

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.squareup.leakcanary.RefWatcher
import java.lang.ref.WeakReference

/**
 * Created by G on 2017/8/19 0019.
 */
private lateinit var app: WeakReference<Application>
val appContent by lazy { app.get()!! }
var refWatcher: RefWatcher? = null

object BaseInit {
    fun onCreate(application: Application) {
        app = WeakReference(application)

        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            ARouter.openDebug()
        }
        //内存泄露监管
//        refWatcher = LeakCanary.install(application)
        application.registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build())
        ARouter.init(application)
    }
}