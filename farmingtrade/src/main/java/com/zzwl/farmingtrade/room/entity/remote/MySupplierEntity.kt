package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class MySupplierEntity(
        @SerializedName("list") var list: List<MySupplierItem> = listOf()
)

data class MySupplierItem(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("price") var price: String = "面议",
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("isShip") var isShip: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("isPutaway") var isPutaway: Boolean = false,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("quantity") var quantity: String = "0",
        @SerializedName("minQuantity") var minQuantity: String = "",
        @SerializedName("imageList") var imageList: List<ImageItem> = listOf()
)