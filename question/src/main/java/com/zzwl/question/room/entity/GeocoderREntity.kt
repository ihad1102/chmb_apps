package com.zzwl.question.room.entity
import com.google.gson.annotations.SerializedName



data class GeocoderREntity(
		@SerializedName("status") var status: Int = -1,
		@SerializedName("result") var result: Result = Result()
)

data class Result(
		@SerializedName("addressComponent") var addressComponent: AddressComponent = AddressComponent()
)

data class AddressComponent(
		@SerializedName("province") var province: String = "陕西",
		@SerializedName("city") var city: String = "西安",
		@SerializedName("district") var district: String = "高新区"
)