package com.im.ui.chat.models

import cn.jiguang.imui.commons.models.IUser
import cn.jpush.im.android.api.model.Message


class DefaultUser(message: Message) : IUser {
    private lateinit var id: String
    private lateinit var displayName: String
    private lateinit var avatarFile: String

    var  message: Message = message
        set(value) {
            val from = value.fromUser
            id = from.userName
            displayName = from.displayName ?: from.userName
            avatarFile = from.avatarFile?.path ?: "R.drawable.ic_default_head"
            field = value
        }

    init {
        this.message = message
    }

    override fun getId(): String = id

    override fun getDisplayName(): String = displayName

    override fun getAvatarFilePath(): String = avatarFile
}
