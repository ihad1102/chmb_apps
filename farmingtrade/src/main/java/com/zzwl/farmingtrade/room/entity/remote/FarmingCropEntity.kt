package com.zzwl.farmingtrade.room.entity.remote
import com.google.gson.annotations.SerializedName

data class FarmingCropEntity(
		@SerializedName("plantingId") var plantingId: Int = 0,
		@SerializedName("cropId") var cropId: Int = 0,
		@SerializedName("cropName") var cropName: String = "",
		@SerializedName("principal") var principal: String = "",
		@SerializedName("batch") var batch: String = "",
		@SerializedName("createTime") var createTime: String = "",
		@SerializedName("annual") var annual: Int = 0
)