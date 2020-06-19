package com.zzwl.question.room.entity.common
import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/5.
 */

data class CommentEntity(
		@SerializedName("img_avatar") var imgAvatar: String? = "",
		@SerializedName("name") var name: String? = "",
		@SerializedName("time") var time: String? = "",
		@SerializedName("is_adopt") var isAdopt: Int? = 0,
		@SerializedName("reply") var reply: List<String?>? = listOf(),
		@SerializedName("comment") var comment: String? = "",
		@SerializedName("comment_url") var commentUrl: String? = "",
		@SerializedName("img_url") var imgUrl: List<String?>? = listOf()
)