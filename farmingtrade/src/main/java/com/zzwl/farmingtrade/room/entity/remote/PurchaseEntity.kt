package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class PurchaseEntity(
        @SerializedName("list") var list: List<PurchaseItem> = listOf()

)

data class PurchaseItem(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("cropId") var cropId: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("regions") var regions: String = "",
        @SerializedName("quantity") var quantity: Int = 0,
        @SerializedName("price") var price: String = "面议",
        @SerializedName("packType") var packType: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = "",
        @SerializedName("userVo") var userVo: UserVo3 = UserVo3()
)
data class  UserVo3(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("realname") var realname: String = ""
)
