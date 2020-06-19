package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class PullDownDataEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("userId") var userId: Int = 0,
        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("times") var times: Int = 0,
        @SerializedName("startTime") var startTime: String = "",
        @SerializedName("endTime") var endTime: String = ""
)