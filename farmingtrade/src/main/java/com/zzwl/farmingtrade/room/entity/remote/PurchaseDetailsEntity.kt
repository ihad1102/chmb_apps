package com.zzwl.farmingtrade.room.entity.remote
import com.google.gson.annotations.SerializedName



data class PurchaseDetailsEntity(
		@SerializedName("id") var id: Int = 0,
		@SerializedName("userId") var userId: Int = 0,
		@SerializedName("cropId") var cropId: Int = 0,
		@SerializedName("title") var title: String = "",
		@SerializedName("regions") var regions: String = "",
		@SerializedName("quantity") var quantity: Int = 0,
		@SerializedName("price") var price: String = "面议",
		@SerializedName("packType") var packType: String = "",
		@SerializedName("specification") var specification: String = "",
		@SerializedName("otherInfo") var otherInfo: String = "",
		@SerializedName("userVo") var userVo: UserVo = UserVo(),
		@SerializedName("doing") var doing: Boolean=false,
		@SerializedName("regionList") var regionList: List<Region> = listOf()
)

data class UserVo(
		@SerializedName("id") var id: Int = 0,
		@SerializedName("headimg") var headimg: String = "",
		@SerializedName("realname") var realname: String = "",
		@SerializedName("expertIntro") var expertIntro: String = "无"
)

data class Region(
		@SerializedName("regionName") var regionName: String = ""
)