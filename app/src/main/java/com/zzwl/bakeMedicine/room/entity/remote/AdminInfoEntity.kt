package com.zzwl.bakeMedicine.room.entity.remote
import com.google.gson.annotations.SerializedName



data class AdminInfoEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("regionAddress") var regionAddress: String = "",
    @SerializedName("addressDetail") var addressDetail: String = "",
    @SerializedName("addressX") var addressX: Double = 0.0,
    @SerializedName("addressY") var addressY: Double = 0.0,
    @SerializedName("houseList") var houseList: List<Any> = listOf(),
    @SerializedName("roleList") var roleList: List<Role> = listOf()
)

data class Role(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("houseGroupId") var houseGroupId: Any? = Any(),
    @SerializedName("name") var name: String = "",
    @SerializedName("tel") var tel: String = "",
    @SerializedName("type") var type: Int = 0,
    @SerializedName("createTime") var createTime: Any? = Any(),
    @SerializedName("updateTime") var updateTime: Any? = Any()
)