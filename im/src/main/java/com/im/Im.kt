package com.im

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.*
import cn.jpush.im.api.BasicCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.appContent
import com.g.base.extend.toast
import com.g.base.room.entity.TokenEntity
import com.g.base.token.TokenManage
import com.im.router.ImRouter
import com.im.ui.chat.holder.FarmingViewConst
import com.im.ui.chat.holder.PurchaseViewConst
import com.im.ui.chat.models.CustomMessageConst

object Im {
    private var isInit = false
    const val KEY = "269129c8b8f40aa5148baf7c"
    const val USER_SUFFIX = "Chat-zhxn-"

    val loginStatus by lazy { MutableLiveData<Int>().apply { value = 0 } }
    private var isLoginIng = false
    private val notifyClickListener by lazy { NotificationClick() }

    fun init(app: Application) {
        if (isInit) return
        isInit = true
        JMessageClient.init(app, true)
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_LED and JMessageClient.FLAG_NOTIFY_WITH_LED)
        login(TokenManage.getToken())
    }

    fun login(tokenEntity: TokenEntity?, errorTime: Int = 3) {
        if (errorTime <= 0 || tokenEntity == null) {
            loginStatus.postValue(2)
            return
        }
        if (isLoginIng) return else isLoginIng = true
        val un = "$USER_SUFFIX${tokenEntity.userId}"
        JMessageClient.login(un, tokenEntity.token, object : BasicCallback() {
            override fun isRunInUIThread(): Boolean {
                return true
            }

            override fun gotResult(p0: Int, p1: String?) {
                isLoginIng = false
                if (p0 != 0) {
                    login(tokenEntity, errorTime - 1)
                } else {
                    JMessageClient.registerEventReceiver(notifyClickListener)
                    loginStatus.value = 1
                    isLoginIng = true
                }
            }
        })
    }

    fun loginOut() {
        JMessageClient.unRegisterEventReceiver(notifyClickListener)
        JMessageClient.logout()
        loginStatus.value = 2
        isLoginIng = false
    }

    class NotificationClick {
        fun onEvent(event: NotificationClickEvent) {
            val userName = event.message.fromUser.userName
            ARouter.getInstance().build(ImRouter.ChatActivity)
                    .withString("targetName", userName)
                    .navigation(appContent)
        }
    }
}

class ImMessageRegister : MutableLiveData<ImMessageRegister.MessageData>() {
    private val conversationEvent by lazy { ConversationRefreshEventListener() }
    private val messageReceiptEvent by lazy { MessageReceiptEventListener() }
    private val receiveMessageEvent by lazy { ReceiveMessageEventListener() }
    private val receiveOfflineMessageEvent by lazy { ReceiveOfflineMessageEventListener() }

    override fun onActive() {
        super.onActive()
        JMessageClient.registerEventReceiver(conversationEvent)
        JMessageClient.registerEventReceiver(messageReceiptEvent)
        JMessageClient.registerEventReceiver(receiveMessageEvent)
        JMessageClient.registerEventReceiver(receiveOfflineMessageEvent)
    }

    override fun onInactive() {
        super.onInactive()
        JMessageClient.unRegisterEventReceiver(conversationEvent)
        JMessageClient.unRegisterEventReceiver(messageReceiptEvent)
        JMessageClient.unRegisterEventReceiver(receiveMessageEvent)
        JMessageClient.unRegisterEventReceiver(receiveOfflineMessageEvent)
    }

    //消息相关回调 导致改变
    private inner class ConversationRefreshEventListener {
        fun onEvent(event: ConversationRefreshEvent) {
            postValue(MessageData(MessageTypeEnum.ConversationRefreshEvent, event))
        }
    }

    private inner class MessageReceiptEventListener {
        fun onEvent(event: MessageReceiptStatusChangeEvent) {
            postValue(MessageData(MessageTypeEnum.MessageReceiptEventListener, event))
        }
    }

    private inner class ReceiveMessageEventListener {
        fun onEvent(event: MessageEvent) {
            postValue(MessageData(MessageTypeEnum.ReceiveMessageEventListener, event))
        }
    }

    private inner class ReceiveOfflineMessageEventListener {
        fun onEvent(event: OfflineMessageEvent) {
            postValue(MessageData(MessageTypeEnum.ReceiveOfflineMessageEventListener, event))
        }
    }

    enum class MessageTypeEnum {
        ConversationRefreshEvent,
        MessageReceiptEventListener,
        ReceiveMessageEventListener,
        ReceiveOfflineMessageEventListener
    }

    data class MessageData(val type: MessageTypeEnum, val ev: Any)
}

object ImCreateHelp {
    fun createWithFarmingViewMessage(price: String, crop: String, image: String, id: String, targetId: String) {
        val token = TokenManage.getToken()
        if(targetId == token?.userId) {
            appContent.toast("您不能和自己聊天")
            return
        }
        val hashMapOf = hashMapOf(Pair(CustomMessageConst.Id, id),
                Pair(CustomMessageConst.Type, CustomMessageConst.ValueFarmingType.toString()),
                Pair(FarmingViewConst.Price,if(price == "面议") price else "$price  元 / 公斤"),
                Pair(FarmingViewConst.Crop, crop),
                Pair(FarmingViewConst.Image, image))
        ARouter.getInstance().build(ImRouter.ChatActivity)
                .withString("targetName", "${Im.USER_SUFFIX}$targetId")
                .withObject("customMessage", hashMapOf)
                .navigation(appContent)
    }

    fun createWithPurchaseViewMessage(price: String, crop: String, amount: String, id: String, targetId: String) {
        val token = TokenManage.getToken()
        if(targetId == token?.userId) {
            appContent.toast("您不能和自己聊天")
            return
        }
        val hashMapOf = hashMapOf(Pair(CustomMessageConst.Id, id),
                Pair(CustomMessageConst.Type, CustomMessageConst.ValuePurchaseType.toString()),
                Pair(PurchaseViewConst.Price, "$price / 元"),
                Pair(PurchaseViewConst.Crop, crop),
                Pair(PurchaseViewConst.Amount, "$amount / 公斤"))
        ARouter.getInstance().build(ImRouter.ChatActivity)
                .withString("targetName", "${Im.USER_SUFFIX}$targetId")
                .withObject("customMessage", hashMapOf)
                .navigation(appContent)
    }
}