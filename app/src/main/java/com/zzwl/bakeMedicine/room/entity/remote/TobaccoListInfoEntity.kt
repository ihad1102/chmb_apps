package com.zzwl.bakeMedicine.room.entity.remote
import com.google.gson.annotations.SerializedName




data class TobaccoListInfoEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("gatewayId") var gatewayId: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("regionId") var regionId: Int = 0,
        @SerializedName("regionAddress") var regionAddress: String = "",
        @SerializedName("addressDetail") var addressDetail: String = "",
        @SerializedName("addressX") var addressX: Double = 0.0,
        @SerializedName("addressY") var addressY: Double = 0.0,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = "",
        @SerializedName("houseInfoList") var houseInfoList: List<HouseInfo> = listOf(),
        @SerializedName("normalCount") var normalCount: Int = 0,
        @SerializedName("alarmCount") var alarmCount: Int = 0,
        @SerializedName("stopCount") var stopCount: Int = 0
)

data class HouseInfo(
    @SerializedName("houseId") var houseId: Int = 0,
    @SerializedName("workStatus") var workStatus: Int = 0,
    @SerializedName("name") var name: String = ""
)