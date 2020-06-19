package com.zzwl.information.room.entity
import com.google.gson.annotations.SerializedName



data class InformationEntity(
    @SerializedName("list") var list: List<InfoEntity> = listOf()
)

data class InfoEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("titleImageId") var titleImageId: Int = 0,
    @SerializedName("source") var source: String = "",
    @SerializedName("publishTime") var createTime: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("contentType") var contentType: Int = 0,
    @SerializedName("titleImage") var titleImage: TitleImage = TitleImage()
)

data class TitleImage(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("url") var url: String = ""
)