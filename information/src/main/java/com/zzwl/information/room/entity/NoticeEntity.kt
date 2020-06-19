package com.zzwl.information.room.entity
import com.google.gson.annotations.SerializedName



data class NoticeEntity(
    @SerializedName("list") var list: List<Notice> = listOf()
)

data class Notice(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("createTime") var createTime: String = "",
    @SerializedName("content") var content: String = ""
)