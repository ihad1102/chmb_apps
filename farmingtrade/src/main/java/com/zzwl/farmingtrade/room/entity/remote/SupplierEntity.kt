package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class SupplierEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("cropId") var cropId: Int = 0,
        @SerializedName("price") var price: String = "面议",
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("isShip") var isShip: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("quantity") var quantity: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("plantingId") var plantingId: Int = 0,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("address") var address: String = "",
        @SerializedName("otherInfo") var otherInfo: String = "",
        @SerializedName("userVo") var userInfo: UserInfo = UserInfo(),
        @SerializedName("imageList") var imageList: List<Image> = listOf(),
        @SerializedName("cropVo") var cropVo: CropVo = CropVo()
)

data class Image(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("url") var url: String = ""
)

data class CropVo(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = ""
)

data class UserInfo(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("realname") var realname: String = "",
        @SerializedName("headimg") var headimg: String = ""
)