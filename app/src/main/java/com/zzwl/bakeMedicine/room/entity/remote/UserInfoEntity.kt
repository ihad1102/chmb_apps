package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class UserInfoEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("username") var username: String = "",
        @SerializedName("realName") var realName: String = "",
        @SerializedName("imageId") var imageId: Int = 0,
        @SerializedName("tel") var tel: String = "",
        @SerializedName("sex") var sex: Boolean = false,
        @SerializedName("age") var age: Int = 0,
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("regionAddress") var regionAddress: String = "",
        @SerializedName("addressDetail") var addressDetail: String = "",
        @SerializedName("headImage") var headImage: HeadImage = HeadImage()
)

data class HeadImage(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("url") var url: String = ""
)