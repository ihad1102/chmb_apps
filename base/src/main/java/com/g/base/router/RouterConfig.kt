package com.g.base.router

/**
 * Created by G on 2017/8/19 0019.
 */

class BasePage {
    companion object {
        const val Oauth ="/activity/oauth"
        const val Launch ="/activity/main"
        const val Home ="/activity/home"
    }
}

class RouterSever{
    companion object {
        //全局降级服务服务 //找不到相关页面的时候
        const val DegradeService = "/service/degrade"
        //Json
        const val JsonService = "/service/json"
    }
}

class RouteExtras{
    companion object {
        const val NeedOauth = 10
    }
}