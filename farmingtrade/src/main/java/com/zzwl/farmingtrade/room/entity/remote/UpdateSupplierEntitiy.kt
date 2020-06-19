package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName


data class UpdateSupplierEntitiy(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("title") var title: String = "",
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("cropId") var cropId: Int = 0,
        @SerializedName("plantingId") var plantingId: Int = 0,
        @SerializedName("quantity") var quantity: Int = 0,
        @SerializedName("minQuantity") var minQuantity: Int = 0,
        @SerializedName("price") var price: Double = 0.0,
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("isShip") var isShip: Boolean = false,
        @SerializedName("isRetrospect") var isRetrospect: Boolean = false,
        @SerializedName("packType") var packType: String = "",
        @SerializedName("otherInfo") var otherInfo: String = "",
        @SerializedName("specification") var specification: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("isPutaway") var isPutaway: Boolean = false,
        @SerializedName("userVo") var userVo: UserVo2 = UserVo2(),
        @SerializedName("imageList") var imageList: List<Image1> = listOf(),
        @SerializedName("cropVo") var cropVo: CropVo1 = CropVo1(),
        @SerializedName("plantingVo") var plantingVo: PlantingVo = PlantingVo(),
        @SerializedName("address") var address: String = ""
)

data class Image1(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("url") var url: String = ""
)

data class CropVo1(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = ""
)

data class UserVo2(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("headimg") var headimg: String = "",
        @SerializedName("realname") var realname: String = ""
)

data class PlantingVo(
        @SerializedName("plantingId") var plantingId: Int = 0,
        @SerializedName("annual") var annual: Int = 0
)