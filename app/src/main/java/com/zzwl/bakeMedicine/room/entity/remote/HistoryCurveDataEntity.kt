package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class HistoryCurveDataEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("dryBulbTemp") var dryBulbTemp: Float = 0f,
        @SerializedName("wetBulbTemp") var wetBulbTemp: Float = 0f,
        @SerializedName("dryBulbGoalTemp") var dryBulbGoalTemp: Float = 0f,
        @SerializedName("wetBulbGoalTemp") var wetBulbGoalTemp: Float = 0f,
        @SerializedName("equipmentTime") var equipmentTime: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = ""
)