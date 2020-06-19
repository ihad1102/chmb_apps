package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName



data class HotPumpTobaccoStatusEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("houseId") var houseId: Int = 0,
    @SerializedName("machineStatus") var machineStatus: Int = 0,
    @SerializedName("workStatus") var workStatus: Int = 0,
    @SerializedName("currentFormula") var currentFormula: Int = 0,
    @SerializedName("currentNumber") var currentNumber: Int = 0,
    @SerializedName("currentRunningTime") var currentRunningTime: Int = 0,
    @SerializedName("totalTime") var totalTime: Int = 0,
    @SerializedName("remainingTime") var remainingTime: Int = 0,
    @SerializedName("currentTemp") var currentTemp: Int = 0,
    @SerializedName("setTemp") var setTemp: Int = 0,
    @SerializedName("currentWetness") var currentWetness: Int = 0,
    @SerializedName("setWetness") var setWetness: Int = 0,
    @SerializedName("compressor1") var compressor1: Compressor1 = Compressor1(),
    @SerializedName("compressor2") var compressor2: Compressor2 = Compressor2()
)

data class Compressor1(
    @SerializedName("auxiliaryHeating") var auxiliaryHeating: Boolean = false,
    @SerializedName("electricalHeating") var electricalHeating: Boolean = false
)

data class Compressor2(
    @SerializedName("auxiliaryHeating") var auxiliaryHeating: Boolean = false
)