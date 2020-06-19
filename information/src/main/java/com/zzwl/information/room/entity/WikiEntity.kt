package com.zzwl.information.room.entity
import com.google.gson.annotations.SerializedName



data class WikiEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("englishName") var englishName: String = "",
    @SerializedName("category") var category: Int = 0,
    @SerializedName("intro") var intro: String = "",
    @SerializedName("imageId") var imageId: Int = 0,
    @SerializedName("createTime") var createTime: String = "",
    @SerializedName("updateTime") var updateTime: String = "",
    @SerializedName("imageUrl") var imageUrl: String = ""
)