package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class HistoryWarningEntityList(
        @SerializedName("list") var list: List<HistoryWarningEntity> = listOf()
)

data class HistoryWarningEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("alarmId") var alarmId: Int = 0,
        @SerializedName("alarmName") var alarmName: String = "",
        @SerializedName("level") var level: Int = 0,
        @SerializedName("status") var status: Int = 0,
        @SerializedName("recoverType") var recoverType: Int = 0,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = "",
        @SerializedName("houseName") var houseName: String = ""
)