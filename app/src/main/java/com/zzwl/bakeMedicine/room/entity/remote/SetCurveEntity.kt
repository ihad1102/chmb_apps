package com.zzwl.bakeMedicine.room.entity.remote
import com.google.gson.annotations.SerializedName



data class SetCurveEntity(
    @SerializedName("houseId") var houseId: Int = -1,
    @SerializedName("leafOption") var leafOption: String = "",
    @SerializedName("selfSettings") var selfSettings: ArrayList<SelfSetting> = arrayListOf()
)

