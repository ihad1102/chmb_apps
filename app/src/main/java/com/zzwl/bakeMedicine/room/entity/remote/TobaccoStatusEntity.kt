package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName


data class TobaccoStatusEntity(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("houseId") var houseId: Int = 0,
        @SerializedName("houseGroupId") var houseGroupId: Int = 0,
        @SerializedName("workStatus") var workStatus: Int = 0,
        @SerializedName("controlShed") var controlShed: Int = 0,
        @SerializedName("temperatureStatus") var temperatureStatus: Int = 0,
        @SerializedName("fanStatus") var fanStatus: Int = 0,
        @SerializedName("combustionOutputStatus") var combustionOutputStatus: Int = 0,
        @SerializedName("arefactionStatus") var arefactionStatus: Int = 0,
        @SerializedName("iconTrumpet") var iconTrumpet: Int = 0,
        @SerializedName("iconKey") var iconKey: Int = 0,
        @SerializedName("iconStandbyCell") var iconStandbyCell: Int = 0,
        @SerializedName("iconFrequency") var iconFrequency: Int = 0,
        @SerializedName("iconNetwork") var iconNetwork: Int = 0,
        @SerializedName("leaf") var leaf: Int = 0,
        @SerializedName("isPartialTemperature") var isPartialTemperature: Boolean = false,
        @SerializedName("isPhaseLoss") var isPhaseLoss: Boolean = false,
        @SerializedName("isOverload") var isOverload: Boolean = false,
        @SerializedName("workStep") var workStep: Float = 0f,
        @SerializedName("dryBulbTemp") var dryBulbTemp: Float = 0f,
        @SerializedName("wetBulbTemp") var wetBulbTemp: Float = 0f,
        @SerializedName("dryBulbGoalTemp") var dryBulbGoalTemp: Float = 0f,
        @SerializedName("wetBulbGoalTemp") var wetBulbGoalTemp: Float = 0f,
        @SerializedName("powerVoltage") var powerVoltage: Int = 0,
        @SerializedName("equipmentTime") var equipmentTime: String = "",
        @SerializedName("workStepTime") var workStepTime: Float = 0f,
        @SerializedName("workTotalTime") var workTotalTime: Float = 0f,
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("updateTime") var updateTime: String = "",
        @SerializedName("theOtherDryBulbTemp") var theOtherDryBulbTemp: Float = 0f,
        @SerializedName("theOtherWetBulbTemp") var theOtherWetBulbTemp: Float = 0f,
        @SerializedName("relativeHumidity") var relativeHumidity: String = ""
)