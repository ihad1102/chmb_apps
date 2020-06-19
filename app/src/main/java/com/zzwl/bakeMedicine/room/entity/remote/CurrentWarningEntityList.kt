package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class CurrentWarningEntityList(
        @SerializedName("list") var list: List<CurrentWarningEntity> = listOf()
)

data class CurrentWarningEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("houseGroupId") var houseGroupId: Int = 0,
        @SerializedName("alarmId") var alarmId: Int = 0,
        @SerializedName("alarmName") var alarmName: String = "",
        @SerializedName("level") var level: Int = 0,
        @SerializedName("status") var status: Int = 0,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = "",
        @SerializedName("houseName") var houseName: String = ""
)