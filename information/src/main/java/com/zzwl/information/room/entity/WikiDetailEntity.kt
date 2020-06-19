package com.zzwl.information.room.entity
import com.google.gson.annotations.SerializedName



data class WikiDetailEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("englishName") var englishName: String = "",
    @SerializedName("synonymName") var synonymName: String = "",
    @SerializedName("category") var category: Int = 0,
    @SerializedName("intro") var intro: String = "",
    @SerializedName("imageId") var imageId: Any? = Any(),
    @SerializedName("createTime") var createTime: String = "",
    @SerializedName("updateTime") var updateTime: String = "",
    @SerializedName("imageUrl") var imageUrl: String? = "",
    @SerializedName("contentList") var contentList: List<Content> = listOf()
)

data class Content(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("encyclopediaId") var encyclopediaId: Any? = Any(),
    @SerializedName("head") var head: String = "",
    @SerializedName("createTime") var createTime: Any? = Any(),
    @SerializedName("updateTime") var updateTime: Any? = Any(),
    @SerializedName("detail") var detail: String = ""
)