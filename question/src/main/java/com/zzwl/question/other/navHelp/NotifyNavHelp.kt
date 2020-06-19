package com.zzwl.question.other.navHelp

import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.appContent
import com.zzwl.question.room.entity.remote.NavREntity
import com.zzwl.question.router.RouterPage

/**
 * Created by G on 2018/1/17 0017.
 */
interface NotifyNode {
    companion object {
        const val NOTIFY_INVITE_ME = "invite_me"//邀请我
        const val NOTIFY_REPLY = "reply"//收到回复
        const val NOTIFY_COMMENT = "comment"//收到评论
        const val NOTIFY_DOVERIFY_OK = "doverify_ok"//审核通过
        const val NOTIFY_DOVERIFY_REJECT = "doverify_reject"//审核失败
        const val NOTIFY_SHAPPING = "shapping"//物流
        const val NOTIFY_HTML_WEB = "html_web"//网页跳转
        const val NOTIFY_SIMPLY = "simply"//不可跳转消息
    }
}

fun navHandle(navREntity: NavREntity) {
    when {
        navREntity.node == NotifyNode.NOTIFY_INVITE_ME || navREntity.node == NotifyNode.NOTIFY_REPLY || navREntity.node == NotifyNode.NOTIFY_COMMENT -> {
            ARouter.getInstance().build(RouterPage.QuestionDetailsActivity).withInt("id", navREntity.detail["id"]?.toIntOrNull()
                    ?: 0).navigation(appContent)
        }
        navREntity.node == NotifyNode.NOTIFY_SHAPPING -> {
//            ARouter.getInstance().build(RouterPage.OrderDetailsActivity).withString("id",navREntity.detail["id"]).navigation(appContent)
        }
        navREntity.node == NotifyNode.NOTIFY_HTML_WEB -> {
//            ARouter.getInstance().build(RouterPage.WebViewActivity).withString("url",navREntity.detail["url"]).navigation(appContent)
        }
    }
}