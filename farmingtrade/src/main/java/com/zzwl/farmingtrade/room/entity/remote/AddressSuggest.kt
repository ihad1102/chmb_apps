package com.zzwl.farmingtrade.room.entity.remote

import com.google.gson.annotations.SerializedName

data class AddressSuggestCEntity(
        @SerializedName("province") var province : String = "",
        @SerializedName("city") var city : String = ""
)

data class OriginAddressSuggestREntity(
		@SerializedName("regionId") var regionId: String = "",
		@SerializedName("parentId") var parentId: String = "",
		@SerializedName("regionName") var regionName: String = "",
		@SerializedName("regionType") var regionType: Int = 0
)