package com.zzwl.bakeMedicine.room.entity.common
import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/2.
 */



data class FocusedCropsEntity(
		@SerializedName("name") var name: String? = "",
		@SerializedName("img_url") var imgUrl: String? = ""
)