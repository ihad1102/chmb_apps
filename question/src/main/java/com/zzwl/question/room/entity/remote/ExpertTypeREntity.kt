package com.zzwl.question.room.entity.remote
import com.google.gson.annotations.SerializedName



data class ExpertTypeREntity(
		@SerializedName("userVerifiedCategoryId") var userVerifiedCategoryId: Int = 0,
		@SerializedName("title") var title: String = "",
		@SerializedName("pid") var pid: Int = 0,
		@SerializedName("sort") var sort: Int = 0
)