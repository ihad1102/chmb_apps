package com.zzwl.bakeMedicine

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.SDKInitializer
import com.g.base.BaseInit
import com.g.base.token.TokenManage
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import interfaces.heweather.com.interfacesmodule.view.HeConfig
import org.greenrobot.eventbus.EventBus

class MyApp : Application() {
    private val tokenLiveData by lazy { TokenManage.getTokenLive() }
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(this)
        BaseInit.onCreate(this)
        EventBus.builder().addIndex(MyIndex()).installDefaultEventBus()
        buglyInit()
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        HeConfig.init("HE1807181532091319", "6118132d68114659b137e15ee02aeb5c")
        HeConfig.switchToFreeServerNode()
        obsTokenChange()
    }

    private fun buglyInit() {
        Beta.initDelay = 5 * 1000L
        Beta.enableHotfix = false
        Bugly.init(this, "cb8fda1e06", true)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun obsTokenChange() {
        tokenLiveData.observeForever {
            if (it?.token == null) {
                TokenManage.cleanToken()
                getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().clear().apply()
                JPushInterface.deleteAlias(this, 0)
            } else {
                //极光别名
                JPushInterface.setAlias(this, 0, it.userId)
            }
        }
    }
}
