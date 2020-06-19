package com.g.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Created by fhf11991 on 2016/7/18.
 */

class ActivityLifecycleHelper private constructor() : Application.ActivityLifecycleCallbacks {

    companion object {
        private var singleton: ActivityLifecycleHelper? = null
        private var activities: MutableList<WeakReference<Activity>>? = null
        private val lockObj = Any()

        fun build(): ActivityLifecycleHelper {
            synchronized(lockObj) {
                if (singleton == null) {
                    singleton = ActivityLifecycleHelper()
                }
                return singleton!!
            }
        }

        fun getLatestActivity(): Activity? = activities?.getOrNull((activities?.size ?: -1) - 1)?.get()

        fun getPreviousActivity(): Activity? = activities?.getOrNull((activities?.size ?: -1) - 2)?.get()
    }

    init {
        activities = ArrayList()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activities == null) {
            activities = ArrayList()
        }
        activities?.add(WeakReference(activity))
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities?.let {
            for (item in it.asReversed()) {
                val get = item.get()
                if (get?.equals(activity) == true){
                    activities?.remove(item)
                    break
                }
            }
        }
        if (activities?.size == 0) {
            activities = null
        }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

}
