package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class UpdatePurchaseEntity(
        @SerializedName("id") var id: String = "0",
        @SerializedName("userId") var userId: String = "0",
        @SerializedName("cropId") var cropId: String = "0",
        @SerializedName("title") var title: String = "",
        @SerializedName("regions") var regions: String = "",
        @SerializedName("quantity") var quantity: String = "0",
        @SerializedName("price") var price: String = "面议",
        @SerializedName("doing") var doing: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("otherInfo") var otherInfo: String = "",
        @SerializedName("userVo") var userVo: UserVo1 = UserVo1(),
        @SerializedName("regionList") var regionList: List<Region1> = listOf()
)

data class Region1(
        @SerializedName("regionName") var regionName: String = "",
        @SerializedName("regionId") var regionId: String = ""
)

data class UserVo1(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("headimg") var headimg: String = "",
        @SerializedName("realname") var realname: String = "",
        @SerializedName("expertIntro") var expertIntro: String = ""
)