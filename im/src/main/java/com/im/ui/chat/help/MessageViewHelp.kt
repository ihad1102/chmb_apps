package com.im.ui.chat.help

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.os.PowerManager
import android.text.format.DateFormat
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.commons.ImageLoader
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.messages.CustomMsgConfig
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.messages.ViewHolderController
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.VideoContent
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.event.OfflineMessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.android.api.model.UserInfo
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.g.base.extend.setTimeout
import com.g.base.extend.toast
import com.g.base.help.d
import com.g.base.token.TokenManage
import com.im.Im
import com.im.R
import com.im.router.ImRouter
import com.im.ui.browserImage.BrowserImageActivity
import com.im.ui.chat.ChatActivity
import com.im.ui.chat.holder.FarmingViewHolder
import com.im.ui.chat.holder.PurchaseViewHolder
import com.im.ui.chat.models.CustomMessageConst
import com.im.ui.chat.models.MyMessage
import com.im.ui.chat.views.ChatView
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MessageViewHelp : View.OnTouchListener, SensorEventListener, EasyPermissions.PermissionCallbacks {
    companion object {
        private const val TAG = "ChatActivity"
        private const val RC_RECORD_VOICE = 0x0001
        private const val RC_CAMERA = 0x0002
        private const val RC_PHOTO = 0x0003
    }

    private lateinit var activity: ChatActivity
    private lateinit var chatView: ChatView
    private lateinit var conversation: Conversation
    private lateinit var targetId: String

    private lateinit var receiver: HeadsetDetectReceiver
    private lateinit var imm: InputMethodManager
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var powerManager: PowerManager
    private var wakeLock: PowerManager.WakeLock? = null
    private val receiveMessageEvent by lazy { ReceiveMessageEvent() }
    private val receiveOfflineMessageEvent by lazy { ReceiveOfflineMessageEvent() }

    private var isInitLate = false
    lateinit var adapter: MsgListAdapter<MyMessage>

    fun init(activity: ChatActivity, chatView: ChatView, conversation: Conversation, targetId: String) {
        this.activity = activity
        this.chatView = chatView
        this.targetId = targetId
        this.conversation = conversation
        //通知栏相关
        JMessageClient.enterSingleConversation((conversation.targetInfo as UserInfo).userName)
        //接收到新消息
        JMessageClient.registerEventReceiver(receiveMessageEvent)
        JMessageClient.registerEventReceiver(receiveOfflineMessageEvent)
        //传感器相关
        this.imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        registerProximitySensorListener()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        receiver = HeadsetDetectReceiver()
        activity.registerReceiver(receiver, intentFilter)

        //视图相关
        this.chatView = chatView
        this.chatView.initModule()
        initMsgAdapter()

        //监听点击
        this.chatView.setOnTouchListener(this)
        this.chatView.setMenuClickListener(object : OnMenuClickListener {
            override fun onSendTextMessage(input: CharSequence): Boolean {
                if (input.isEmpty()) {
                    return false
                }
                addMessage(conversation.createSendTextMessage(input.toString()))
                scrollToBottom()
                return true
            }

            override fun onSendFiles(list: List<FileItem>?) {
                if (list == null || list.isEmpty()) return
                loop@ for (item in list) {
                    when {
                        item.type == FileItem.Type.Image -> {
                            val photoPath = item.filePath
                            val message = conversation.createSendImageMessage(File(photoPath))
                            addMessage(message)
                        }
                        item.type == FileItem.Type.Video -> {
                            activity.handleWindow(false)
                            val videoPath = item.filePath
                            val media = MediaMetadataRetriever()
                            media.setDataSource(videoPath)
                            val duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
                            val message = JMessageClient.createSingleVideoMessage(targetId, Im.KEY,
                                    media.getFrameAtTime(0L), "png",
                                    File(videoPath), null, duration)
                            addMessage(message)
                        }
                    }
                }
                scrollToBottom()
            }

            override fun switchToMicrophoneMode(): Boolean {
                scrollToBottom()
                val perms = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if (!EasyPermissions.hasPermissions(activity, *perms)) {
                    EasyPermissions.requestPermissions(activity,
                            activity.resources.getString(R.string.rationale_record_voice),
                            RC_RECORD_VOICE, *perms)
                }
                return true
            }

            override fun switchToGalleryMode(): Boolean {
                scrollToBottom()
                val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                if (!EasyPermissions.hasPermissions(activity, *perms)) {
                    EasyPermissions.requestPermissions(activity,
                            activity.resources.getString(R.string.rationale_photo),
                            RC_PHOTO, *perms)
                }
                return true
            }

            override fun switchToCameraMode(): Boolean {
                scrollToBottom()
                val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

                if (!EasyPermissions.hasPermissions(activity, *perms)) {
                    EasyPermissions.requestPermissions(activity,
                            activity.resources.getString(R.string.rationale_camera),
                            RC_CAMERA, *perms)
                } else {
                    val rootDir = activity.filesDir
                    val fileDir = rootDir.absolutePath + "/photo"
                    this@MessageViewHelp.chatView.setCameraCaptureFile(fileDir, SimpleDateFormat("yyyy-MM-dd-hhmmss",
                            Locale.getDefault()).format(Date()))
                }
                return true
            }

            override fun switchToEmojiMode(): Boolean {
                scrollToBottom()
                return true
            }
        })

        this.chatView.setRecordVoiceListener(object : RecordVoiceListener {
            override fun onStartRecord() {
                val path = Environment.getExternalStorageDirectory().path + "/voice"
                val destDir = File(path)
                if (!destDir.exists()) {
                    destDir.mkdirs()
                }
                this@MessageViewHelp.chatView.setRecordVoiceFile(destDir.path, DateFormat.format("yyyy-MM-dd-hhmmss",
                        Calendar.getInstance(Locale.CHINA)).toString() + "")
            }

            override fun onFinishRecord(voiceFile: File?, duration: Int) {
                voiceFile ?: return
                val message = conversation.createSendVoiceMessage(voiceFile, duration)
                addMessage(message)
                scrollToBottom()
            }

            override fun onCancelRecord() {

            }

            override fun onPreviewCancel() {

            }

            override fun onPreviewSend() {

            }
        })

        this.chatView.setOnCameraCallbackListener(object : OnCameraCallbackListener {
            override fun onTakePictureCompleted(photoPath: String?) {
                photoPath ?: return
                val message = conversation.createSendImageMessage(File(photoPath))
                addMessage(message)
                scrollToBottom()
            }

            override fun onStartVideoRecord() {

            }

            override fun onFinishVideoRecord(videoPath: String?) {
                d(videoPath)
            }

            override fun onCancelVideoRecord() {

            }
        })

        this.chatView.chatInputView.inputView.setOnTouchListener { _, _ ->
            scrollToBottom()
            false
        }

        isInitLate = true
    }

    private fun initMsgAdapter() {
        val imageLoader = object : ImageLoader {
            val imageUriMap = HashMap<ImageView, String?>()
            override fun loadAvatarImage(imageView: ImageView, uri: String?) {
                val before = imageUriMap[imageView]
                if (before != null && before == uri) return
                imageUriMap[imageView] = uri

                Glide.with(activity)
                        .load(uri)
                        .asBitmap()
                        .placeholder(R.drawable.ic_default_head)
                        .error(R.drawable.ic_default_head)
                        .crossFade(300)
                        .into(imageView)
            }

            override fun loadImage(imageView: ImageView, uri: String?) {
                val before = imageUriMap[imageView]
                if (before != null && before == uri) return
                imageUriMap[imageView] = uri

                Glide.with(activity)
                        .load(uri)
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .error(R.drawable.aurora_picture_not_found)
                        .crossFade(300)
                        .into(imageView)
            }

            override fun loadVideo(imageView: ImageView, uri: String?) {
                val before = imageUriMap[imageView]
                if (before != null && before == uri) return
                imageUriMap[imageView] = uri

                Glide.with(activity)
                        .load(uri)
                        .asBitmap()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .error(R.drawable.aurora_picture_not_found)
                        .override(200, 400)
                        .crossFade(300)
                        .into(imageView)
            }
        }

        val holdersConfig = MsgListAdapter.HoldersConfig()
        adapter = MsgListAdapter("${TokenManage.getToken()?.userId}${Im.USER_SUFFIX}",
                holdersConfig, imageLoader)

        adapter.setOnMsgClickListener { message ->
            val content = message.message.content
            if (content is VideoContent) {
                if (content.videoLocalPath != null)
                    ARouter.getInstance().build(ImRouter.VideoViewActivity)
                            .withString("videoPath", content.videoLocalPath)
                            .navigation(activity)
                else
                    activity.toast("请等待视频下载完成")
            } else if (content is ImageContent) {
                val intent = Intent(activity, BrowserImageActivity::class.java)

                val pathList = arrayListOf<String>()
                val msgIdList = arrayListOf<String>()
                adapter.messageList.forEach {
                    val contentImage = it.message.content
                    if (contentImage is ImageContent) {
                        pathList.add(contentImage.localPath)
                        msgIdList.add(it.msgId)
                    } else if (contentImage is VideoContent) {
                        pathList.add(contentImage.thumbLocalPath)
                        msgIdList.add(it.msgId)
                    }
                }

                intent.putExtra("msgId", message.msgId)
                intent.putStringArrayListExtra("pathList", pathList)
                intent.putStringArrayListExtra("idList", msgIdList)
                activity.startActivity(intent)
            }
        }

        adapter.setMsgStatusViewClickListener {
            if (it.messageStatus == IMessage.MessageStatus.SEND_FAILED) {
                it.onSend()
            }
        }

        adapter.addToEnd(conversation.getMessagesFromNewest(0, 20).map { MyMessage(it, adapter) })

        chatView.ptrLayout.setPtrHandler {
            setTimeout(200L) {
                val messagesFromNewest = conversation.getMessagesFromNewest(adapter.messageList.size, 20)
                if (messagesFromNewest == null || messagesFromNewest.isEmpty())
                    activity.toast("没有更多了!")
                else
                    adapter.addToEnd(messagesFromNewest.map { MyMessage(it, adapter) })
                chatView.ptrLayout.refreshComplete()
            }
        }

        setCustomMessageView()

        chatView.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        scrollToBottom()
    }

    private fun setCustomMessageView() {
        val purchaseType = CustomMsgConfig(CustomMessageConst.ValuePurchaseType, R.layout.item_purchase, true, PurchaseViewHolder::class.java)
        val farmingType = CustomMsgConfig(CustomMessageConst.ValueFarmingType, R.layout.item_farming, true, FarmingViewHolder::class.java)
        adapter.addCustomMsgType(CustomMessageConst.ValuePurchaseType, purchaseType)
        adapter.addCustomMsgType(CustomMessageConst.ValueFarmingType, farmingType)
    }

    fun addMessage(message: Message, scrollToBottom: Boolean = false) {
        activity.runOnUiThread {
            adapter.addToStart(MyMessage(message, adapter), scrollToBottom)
        }
    }

    //------------------------------------------------------传感器及其反注册相关

    //传感器屏幕锁屏相关
    @SuppressLint("InvalidWakeLockTag")
    private fun registerProximitySensorListener() {
        try {
            powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG)
            sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //传感器状态改变
    //贴近耳朵的时候关闭屏幕 远离的时候打开屏幕
    override fun onSensorChanged(event: SensorEvent) {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            if (audioManager.isBluetoothA2dpOn || audioManager.isWiredHeadsetOn) {
                return
            }
            if (adapter.mediaPlayer.isPlaying) {
                val distance = event.values[0]
                if (distance >= sensor.maximumRange) {
                    adapter.setAudioPlayByEarPhone(0)
                    setScreenOn()
                } else {
                    adapter.setAudioPlayByEarPhone(2)
                    ViewHolderController.getInstance().replayVoice()
                    setScreenOff()
                }
            } else {
                val wakeLock = this.wakeLock
                if (wakeLock != null && wakeLock.isHeld) {
                    wakeLock.release()
                    this.wakeLock = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setScreenOn() {
        val wakeLock = this.wakeLock
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false)
            wakeLock.release()
            this.wakeLock = null
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun setScreenOff() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG)
        }
        wakeLock!!.acquire(1000L)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    //贴近耳朵的时候用听筒播放声音
    private inner class HeadsetDetectReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                if (intent.hasExtra("state")) {
                    val state = intent.getIntExtra("state", 0)
                    adapter.setAudioPlayByEarPhone(state)
                }
            }
        }
    }

    //权限申请
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            AppSettingsDialog.Builder(activity).build().show()
        }
    }

    private fun scrollToBottom() {
        setTimeout(200L) { chatView.messageListView.smoothScrollToPosition(0) }
    }

    //点击消息列表的时候收起软键盘
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val chatInputView = chatView.chatInputView
                if (chatInputView.menuState == View.VISIBLE) {
                    chatInputView.dismissMenuLayout()
                }
                try {
                    val v = activity.currentFocus
                    if (v != null) {
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        view.clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            MotionEvent.ACTION_UP -> view.performClick()
        }
        return false
    }

    fun onDestroy() {
        if (isInitLate) {
            //极光消息反注册
            JMessageClient.unRegisterEventReceiver(receiveMessageEvent)
            JMessageClient.unRegisterEventReceiver(receiveOfflineMessageEvent)
            JMessageClient.exitConversation()
            conversation.resetUnreadCount()
            //传感器反注册
            activity.unregisterReceiver(receiver)
            sensorManager.unregisterListener(this)
        }
    }

    inner class ReceiveMessageEvent {
        fun onEvent(ev: MessageEvent) {
            val message = ev.message
            if (message.fromUser.userName != targetId) return
            addMessage(message)
            scrollToBottom()
        }
    }

    inner class ReceiveOfflineMessageEvent {
        fun onEvent(ev: OfflineMessageEvent) {
            if (ev.conversation.id == conversation.id) {
                ev.offlineMessageList.forEach {
                    addMessage(it)
                }
                scrollToBottom()
            }
        }
    }
}