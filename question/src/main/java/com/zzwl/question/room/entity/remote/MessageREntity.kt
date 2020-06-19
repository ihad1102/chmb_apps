package com.zzwl.question.room.entity.remote

import com.google.gson.annotations.SerializedName


/**
 * Created by G on 2017/11/26 0026.
 */

data class MessageREntity(
        @SerializedName("id") var id: String = "",
        @SerializedName("msgId") var msgId: String = "",
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("subtitle") var subtitle: String = "",
        @SerializedName("isRead") var read: Int = 0,
        @SerializedName("createTime") var date: String = "",
        @SerializedName("node") var node: String = "",
        @SerializedName("ext") var detail: Map<String,String> = HashMap(),
        var navParams : NavREntity = NavREntity()
)