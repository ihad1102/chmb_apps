package com.im.ui.chat


import android.os.Bundle
import cn.jiguang.imui.chatinput.listener.CameraControllerListener
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.model.Conversation
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeNullableEx
import com.g.base.extend.toast
import com.g.base.room.entity.TokenEntity
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.im.Im
import com.im.R
import com.im.databinding.ActivityChatBinding
import com.im.router.ImRouter
import com.im.ui.chat.help.MessageViewHelp
import com.im.ui.chat.models.CustomMessageConst
import java.util.*

@Route(path = ImRouter.ChatActivity, extras = RouteExtras.NeedOauth)
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    override var hasToolbar: Boolean = true

    @Autowired
    @JvmField
    var targetName: String? = ""

    @Autowired
    @JvmField
    var customMessage: HashMap<String, String>? = null

    private val conversation: Conversation? by lazy {
        JMessageClient.getSingleConversation(targetName, Im.KEY)
                ?: Conversation.createSingleConversation(targetName, Im.KEY)
    }
    private val messageViewHelp = MessageViewHelp()

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null) showNeedOauth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //异常保护
        ARouter.getInstance().inject(this)
        if (targetName.isNullOrEmpty() ||
                targetName == "${Im.USER_SUFFIX}null" ||
                targetName == Im.USER_SUFFIX) {
            toast("聊天对象不能为空")
            finish()
            return
        }
        //初始化视图
        setContentView(R.layout.activity_chat)
        toolbar.title = "登录中..."
        Im.login(TokenManage.getToken())
        Im.loginStatus.observeNullableEx(this) {
            when (it) {
                0 -> {
                    showLoading()
                }
                1 -> {
                    init()
                }
                2 -> {
                    toast("登录异常")
                    finish()
                }
            }
        }
    }

    private fun init() {
        if(conversation == null){
            toast("连接Im服务器异常,请尝试重启程序")
            finish()
            return
        }
        toolbar.title = conversation!!.title
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        messageViewHelp.init(this, contentView.chatView, conversation!!, targetName!!)
        setListener()
        sendCustomMessage()
        showContentView()
    }

    private fun sendCustomMessage() {
        customMessage ?: return
        val id = customMessage!![CustomMessageConst.Id]
        val type = customMessage!![CustomMessageConst.Type]

        val firstOrNull = conversation!!.getMessagesFromNewest(0, 20).firstOrNull {
            val customContent = it.content as? CustomContent ?: return@firstOrNull false
            customContent.getStringValue(CustomMessageConst.Id) == id &&
                    customContent.getStringValue(CustomMessageConst.Type) == type
        }
        if (firstOrNull != null) return
        messageViewHelp.addMessage(conversation!!.createSendCustomMessage(customMessage))
    }

    private fun setListener() {
        contentView.chatInput.setCameraControllerListener(object : CameraControllerListener {
            override fun onFullScreenClick() {
                handleWindow(true)
            }

            override fun onRecoverScreenClick() {
                if (contentView.chatInput.isFullScreen)
                    handleWindow(false)
            }

            override fun onSwitchCameraModeClick(isRecordVideoMode: Boolean) {
                if (isRecordVideoMode) {
                    handleWindow(true)
                } else {
                    handleWindow(false)
                }
                onRecoverScreenClick()
            }

            override fun onCloseCameraClick() {
                handleWindow(false)
            }
        })
    }


    fun handleWindow(full: Boolean) {
        if (full) {
            if (isFullScreen) return
            isFullScreen = true
            hasToolbar = false
            handlerWindowInset()
        } else {
            if (!isFullScreen) return
            isFullScreen = false
            hasToolbar = true
            handlerWindowInset()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        messageViewHelp.onDestroy()
    }
}
