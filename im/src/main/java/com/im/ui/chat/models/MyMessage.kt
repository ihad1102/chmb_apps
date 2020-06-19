package com.im.ui.chat.models

import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.commons.models.IMessage.MessageStatus
import cn.jiguang.imui.commons.models.IMessage.MessageType
import cn.jiguang.imui.commons.models.IUser
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.DownloadCompletionCallback
import cn.jpush.im.android.api.callback.ProgressUpdateCallback
import cn.jpush.im.android.api.content.*
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.options.MessageSendingOptions
import cn.jpush.im.api.BasicCallback
import com.g.base.help.d
import com.im.help.toFormatDate
import java.io.File
import java.util.*


class MyMessage(var message: Message, val adapter: MsgListAdapter<MyMessage>) : IMessage {
    private var text: String = ""
    private var user: IUser = DefaultUser(message)
    private var msgStatus: MessageStatus = MessageStatus.CREATED

    private var timeString: String

    private var type: Int = 0

    private var duration: Long = 0L
    private var progress: String = ""
    private var mediaFilePath: String? = ""

    init {
        //消息的时间
        val createTime = message.createTime ?: System.currentTimeMillis()
        timeString = if (System.currentTimeMillis() - createTime > 24 * 60 * 60 * 1000)
            createTime.toFormatDate("MM-dd HH:mm:ss")
        else
            createTime.toFormatDate("HH:mm:ss")
        //根据消息类型和发送接收
        if (message.direct == MessageDirect.send)
            onSend()
        else
            onReceive()
        d(message.contentType)
    }

    //---------------------------------------------------------SEND
    fun onSend() {
        val options = MessageSendingOptions()
        options.isNeedReadReceipt = true
        if (message.status == cn.jpush.im.android.api.enums.MessageStatus.send_fail ||
                message.status == cn.jpush.im.android.api.enums.MessageStatus.created) {
            JMessageClient.sendMessage(message, options)
            message.setOnSendCompleteCallback(object : BasicCallback() {
                override fun gotResult(p0: Int, p1: String?) {
                    msgStatus = if (p0 == 0) {
                        MessageStatus.SEND_SUCCEED
                    } else {
                        MessageStatus.SEND_FAILED
                    }
                    adapter.updateMessage(this@MyMessage)
                }
            })
            message.setOnContentUploadProgressCallback(object : ProgressUpdateCallback() {
                override fun onProgressUpdate(p0: Double) {
                    progress = p0.toString()
                    adapter.updateMessage(this@MyMessage)
                }
            })
        } else {
            msgStatus = MessageStatus.SEND_SUCCEED
            adapter.updateMessage(this@MyMessage)
        }

        when (message.contentType) {
            ContentType.text -> {
                type = MessageType.SEND_TEXT.ordinal
                text = (message.content as TextContent).text
            }
            ContentType.voice -> {
                type = MessageType.SEND_VOICE.ordinal
                val content = message.content as VoiceContent
                duration = content.duration.toLong()
                mediaFilePath = content.localPath
            }
            ContentType.video -> {
                type = MessageType.SEND_VIDEO.ordinal
                val content = message.content as VideoContent
                duration = content.duration / 1000L
                mediaFilePath = content.thumbLocalPath
            }
            ContentType.image -> {
                type = MessageType.SEND_IMAGE.ordinal
                val content = message.content as ImageContent
                mediaFilePath = content.localThumbnailPath
            }
            ContentType.custom -> {
                val customType = (message.content as CustomContent).getStringValue(CustomMessageConst.Type)
                if (customType == null) {
                    type = MessageType.SEND_TEXT.ordinal
                    text = "[错误的自定义消息]"
                } else {
                    type = customType.toInt()
                }
            }
        }
    }

    //---------------------------------------------------------READ
    fun onReceive() {
        message.setOnContentDownloadProgressCallback(object : ProgressUpdateCallback() {
            override fun onProgressUpdate(p0: Double) {
                progress = p0.toString()
                adapter.updateMessage(this@MyMessage)
            }
        })

        when (message.contentType) {
            ContentType.text -> {
                type = MessageType.RECEIVE_TEXT.ordinal
                val content = message.content as TextContent
                text = content.text
                msgStatus = MessageStatus.RECEIVE_SUCCEED
            }
            ContentType.voice -> {
                type = MessageType.RECEIVE_VOICE.ordinal
                val content = message.content as VoiceContent
                content.downloadVoiceFile(message, object : DownloadCompletionCallback() {
                    override fun onComplete(p0: Int, p1: String?, p2: File?) {
                        if (p0 == 0) {
                            mediaFilePath = p2?.path ?: ""
                            msgStatus = MessageStatus.RECEIVE_SUCCEED
                        } else {
                            msgStatus = MessageStatus.RECEIVE_FAILED
                        }
                        adapter.updateMessage(this@MyMessage)
                    }
                })
            }
            ContentType.video -> {
                type = MessageType.RECEIVE_VIDEO.ordinal
                val content = message.content as VideoContent
                content.downloadThumbImage(message, object : DownloadCompletionCallback() {
                    override fun onComplete(p0: Int, p1: String?, p2: File?) {
                        if (p0 == 0) {
                            mediaFilePath = p2?.path ?: ""
                        }
                        adapter.updateMessage(this@MyMessage)
                    }
                })
                content.downloadVideoFile(message, object : DownloadCompletionCallback() {
                    override fun onComplete(p0: Int, p1: String?, p2: File?) {
                        msgStatus = if (p0 == 0) {
                            MessageStatus.RECEIVE_SUCCEED
                        } else {
                            MessageStatus.RECEIVE_FAILED
                        }
                        adapter.updateMessage(this@MyMessage)
                    }
                })
            }
            ContentType.image -> {
                type = MessageType.RECEIVE_IMAGE.ordinal
                val content = message.content as ImageContent
                content.downloadThumbnailImage(message, object : DownloadCompletionCallback() {
                    override fun onComplete(p0: Int, p1: String?, p2: File?) {
                        if (p0 == 0) {
                            mediaFilePath = p2?.path ?: ""
                        }
                        adapter.updateMessage(this@MyMessage)
                    }
                })
                content.downloadOriginImage(message, object : DownloadCompletionCallback() {
                    override fun onComplete(p0: Int, p1: String?, p2: File?) {
                        msgStatus = if (p0 == 0) {
                            MessageStatus.RECEIVE_SUCCEED
                        } else {
                            MessageStatus.RECEIVE_FAILED
                        }
                        adapter.updateMessage(this@MyMessage)
                    }
                })
            }
            ContentType.custom -> {
                val customType = (message.content as CustomContent).getStringValue(CustomMessageConst.Type)
                if (customType == null) {
                    type = MessageType.SEND_TEXT.ordinal
                    text = "[错误的自定义消息]"
                } else {
                    type = customType.toInt()
                }
            }
        }
    }

    fun setMessageStatus(msgStatus: MessageStatus) {
        this.msgStatus = msgStatus
    }

    override fun getMsgId() = message.id.toString()

    override fun getFromUser() = user

    override fun getDuration() = duration

    override fun getProgress() = progress

    override fun getTimeString() = timeString

    override fun getType() = type

    override fun getMessageStatus() = this.msgStatus

    override fun getText() = text

    override fun getMediaFilePath() = mediaFilePath

    override fun getExtras(): HashMap<String, String>? = null
}

object CustomMessageConst {
    const val Id = "Id"
    const val Type = "Type"
    const val ValueFarmingType = 14
    const val ValuePurchaseType = 13
}