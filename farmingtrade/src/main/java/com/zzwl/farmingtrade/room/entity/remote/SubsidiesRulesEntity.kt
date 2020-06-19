package com.zzwl.farmingtrade.room.entity.remote
import com.google.gson.annotations.SerializedName



data class SubsidiesRulesEntity(
		@SerializedName("cropId") var cropId: Int = 0,
		@SerializedName("money") var money: Double = 0.0,
		@SerializedName("minQuantity") var minQuantity: Int = 0
)