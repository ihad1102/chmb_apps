package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class CurveEntity(

        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("leafOption") var leafOption: Int = 0,
        @SerializedName("selfSettings") var selfSettings: ArrayList<SelfSetting> = arrayListOf()
)

data class SelfSetting(
        @SerializedName("dryBulbGoalTemp") var dryBulbGoalTemp: String = "0",
        @SerializedName("wetBulbGoalTemp") var wetBulbGoalTemp: String = "0",
        @SerializedName("tempHoldingTime") var tempHoldingTime: String = "0",
        @SerializedName("tempHeatingTime") var tempHeatingTime: String = "0"
)