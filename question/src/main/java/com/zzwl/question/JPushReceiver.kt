package com.zzwl.question

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.jpush.android.api.JPushInterface
import com.g.base.appContent
import com.g.base.extend.observeNullableExOnce
import com.g.base.help.postSimpleNotify
import com.g.base.help.tryCatch
import com.g.base.room.repository.Status
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.zzwl.question.api.NotifyURl
import com.zzwl.question.other.navHelp.navHandle
import com.zzwl.question.room.entity.remote.NavREntity
import com.zzwl.question.viewModel.MessageRepository

/**
 * Created by G on 2017/11/22 0022.
 */
class JPushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
                tryCatch {
                    postSimpleNotify(NotifyURl.MessageCount)
                    val msgId = intent.extras.getString(JPushInterface.EXTRA_MSG_ID)
                    val notifyCationId = intent.extras.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                    JpushNotifyHelp.messageQueue[msgId] = notifyCationId
                }
            }
            JPushInterface.ACTION_NOTIFICATION_OPENED -> {
                JpushNotifyHelp.markMessageRead(intent.extras.getString(JPushInterface.EXTRA_MSG_ID))
                val json = intent.extras.getString(JPushInterface.EXTRA_EXTRA)
                if (json != null && !json.isEmpty()) {
                    val navREntity = Gson().fromJson(parseJPushParam(json), NavREntity::class.java)
                    navHandle(navREntity)
                }
            }
        }
    }

    private fun parseJPushParam(json: String): String {
        val parse = JsonParser().parse(json).asJsonObject
        return """
                            {
                                "ext" : ${parse.get("ext").asString},
                                "node" : ${parse.get("node")}
                            }
        """.trimIndent()
    }
}

object JpushNotifyHelp {
    val messageQueue by lazy { HashMap<String, Int>() }
    fun cleanMessageNotify(msgId: String) {
        val notifyId = messageQueue[msgId]
        if (notifyId != null) {
            JPushInterface.clearNotificationById(appContent, notifyId)
            messageQueue.remove(msgId)
        }
    }

    fun markMessageRead(msgId: String) {
        var type = ""
        for ((key, messageLive) in MessageRepository.messageData) {
            val filter = messageLive.value?.filter { it.msgId == msgId }
            if (filter?.isNotEmpty() == true) {
                type = key
                break
            }
        }
        messageQueue.remove(msgId)
        MessageRepository().markMessageRead(msgId, type)
                .observeNullableExOnce {
                    if (it!!.status == Status.Content) {
                        postSimpleNotify(NotifyURl.MessageCount)
                    }
                }
    }
}