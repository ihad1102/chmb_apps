package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class MyPurchaseEntity(
        @SerializedName("list") var list: List<MyPurchaseItem> = listOf()
)

data class MyPurchaseItem(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("cropId") var cropId: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("regions") var regions: String = "",
        @SerializedName("price") var price: String = "面议",
        @SerializedName("quantity") var quantity: String = "0",
        @SerializedName("doing") var doing: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("otherInfo") var otherInfo: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = ""
)