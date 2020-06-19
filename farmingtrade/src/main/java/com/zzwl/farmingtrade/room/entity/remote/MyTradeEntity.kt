package com.zzwl.farmingtrade.room.entity.remote
import com.google.gson.annotations.SerializedName



data class MyTradeEntity(
		@SerializedName("list") var list: List<MyTradeItem> = listOf()
)

data class MyTradeItem(
		@SerializedName("id") var id: String = "0",
		@SerializedName("seller") var seller: Int = 0,
		@SerializedName("buyer") var buyer: Int = 0,
		@SerializedName("totalPrice") var totalPrice: Double = 0.00,
		@SerializedName("totalSubsidy") var totalSubsidy: Double = 0.00,
		@SerializedName("status") var status: Int = 0,
		@SerializedName("weight") var weight: String = "0",
		@SerializedName("createTime") var createTime: String = "",
		@SerializedName("updateTime") var updateTime: String = "",
		@SerializedName("sellUser") var sellUser: SellUser = SellUser(),
		@SerializedName("buyUser") var buyUser: BuyUser = BuyUser(),
		@SerializedName("crop") var crop: Crop = Crop()
)

data class SellUser(
		@SerializedName("id") var id: Int = 0,
		@SerializedName("realname") var realname: String = ""
)

data class BuyUser(
		@SerializedName("id") var id: Int = 0,
		@SerializedName("realname") var realname: String = ""
)

data class Crop(
		@SerializedName("name") var name: String = ""
)