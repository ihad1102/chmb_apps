package com.im.ui.chat.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import cn.jiguang.imui.chatinput.ChatInputView
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener
import cn.jiguang.imui.chatinput.listener.OnClickEditTextListener
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.record.RecordVoiceButton
import cn.jiguang.imui.messages.MessageList
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jiguang.imui.messages.ptr.PtrDefaultHeader
import cn.jiguang.imui.messages.ptr.PullToRefreshLayout
import com.g.base.extend.dp
import com.im.R


class ChatView : RelativeLayout {
    lateinit var messageListView: MessageList
    lateinit var chatInputView: ChatInputView
    lateinit var ptrLayout: PullToRefreshLayout
    private lateinit var recordVoiceBtn: RecordVoiceButton

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun initModule() {
        messageListView = findViewById(R.id.msgList)!!
        chatInputView = findViewById(R.id.chatInput)!!
        ptrLayout = findViewById(R.id.ptrl)!!

        //下拉刷新相关
        chatInputView.setMenuContainerHeight(820)
        recordVoiceBtn = chatInputView.recordVoiceButton
        val header = PtrDefaultHeader(context)
        val colors = resources.getIntArray(R.array.google_colors)
        header.setColorSchemeColors(colors)
        header.layoutParams = RelativeLayout.LayoutParams(-1, -2)
        header.setPadding(0, 16.dp(), 0, 10.dp())
        header.setPtrFrameLayout(ptrLayout)
        ptrLayout.setLoadingMinTime(1000)
        ptrLayout.setDurationToCloseHeader(1500)
        ptrLayout.headerView = header
        ptrLayout.addPtrUIHandler(header)
        ptrLayout.isPinContent = true
    }

    override fun fitSystemWindows(insets: Rect?): Boolean {
        insets?.top = 0
        return super.fitSystemWindows(insets)
    }

    fun setMenuClickListener(listener: OnMenuClickListener) {
        chatInputView.setMenuClickListener(listener)
    }

    fun setAdapter(adapter: MsgListAdapter<*>) {
        messageListView.setAdapter(adapter)
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        messageListView.layoutManager = layoutManager
    }

    fun setRecordVoiceFile(path: String, fileName: String) {
        recordVoiceBtn.setVoiceFilePath(path, fileName)
    }

    fun setCameraCaptureFile(path: String, fileName: String) {
        chatInputView.setCameraCaptureFile(path, fileName)
    }

    fun setRecordVoiceListener(listener: RecordVoiceListener) {
        chatInputView.setRecordVoiceListener(listener)
    }

    fun setOnCameraCallbackListener(listener: OnCameraCallbackListener) {
        chatInputView.setOnCameraCallbackListener(listener)
    }

    fun setOnTouchEditTextListener(listener: OnClickEditTextListener) {
        chatInputView.setOnClickEditTextListener(listener)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: View.OnTouchListener) {
        messageListView.setOnTouchListener(listener)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
