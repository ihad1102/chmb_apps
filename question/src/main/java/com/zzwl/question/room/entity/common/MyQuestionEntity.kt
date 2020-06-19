package com.zzwl.question.room.entity.common
import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/9.
 */

data class MyQuestionEntity(
		@SerializedName("id") var id: Int? = 0,
		@SerializedName("img_url") var imgUrl: List<String?>? = listOf(),
		@SerializedName("content") var content: String? = "",
		@SerializedName("time") var time: String? = ""
)