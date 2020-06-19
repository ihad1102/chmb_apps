package com.zzwl.farmingtrade.room.entity.remote
import com.google.gson.annotations.SerializedName



data class PickupPointREntity(
		@SerializedName("id") var id: String = "",
		@SerializedName("name") var name: String = "",
		@SerializedName("address") var address: String = "",
		@SerializedName("phone") var phone: String = "",
		@SerializedName("contact") var contact: String = "",
		@SerializedName("provinceId") var provinceId: String = "",
		@SerializedName("cityId") var cityId: String = "",
		@SerializedName("districtId") var districtId: String = ""
)