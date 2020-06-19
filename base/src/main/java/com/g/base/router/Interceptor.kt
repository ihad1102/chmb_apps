package com.g.base.router

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.ActivityLifecycleHelper
import com.g.base.token.TokenManage

/**
 * Created by G on 2017/8/23 0023.
 */


@Interceptor(priority = 10, name = "OauthInterceptor")
class OauthInterceptor : IInterceptor {
    var context: Context? = null

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        when {
            postcard?.path != BasePage.Home && ActivityLifecycleHelper.getLatestActivity() == null ->{
                ARouter.getInstance().build(BasePage.Home)
                        .with(postcard?.extras)
                        .withString("launchFinishNav",postcard?.path)
                        .navigation(context)
            }
            (postcard?.extra ?: 0) == RouteExtras.NeedOauth && !TokenManage.isOauth() -> {
                ARouter.getInstance().build(BasePage.Oauth)
                        .with(postcard?.extras)
                        .withString("oauthFinishNav", postcard?.path)
                        .navigation(context)
                callback?.onInterrupt(null)
            }
            else -> callback?.onContinue(postcard)
        }
    }

    override fun init(context: Context?) {
        this.context = context
    }
}