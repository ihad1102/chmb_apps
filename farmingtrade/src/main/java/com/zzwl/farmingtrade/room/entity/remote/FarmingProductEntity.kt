package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class FramingProductEntity(
        @SerializedName("list") var list: List<FarmingProductItem> = listOf()
)

data class FarmingProductItem(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("price") var price: String = "面议",
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("isShip") var isShip: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("imageList") var imageList: List<ImageItem> = listOf(),
        @SerializedName("address") var address: String = "",
        @SerializedName("quantity") var quantity: String = "",
        @SerializedName("userVo") var userVo: UserVo4 = UserVo4()
)

data class ImageItem(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("url") var url: String = ""
        )
data class  UserVo4(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("realname") var realname: String = ""
)