package com.zzwl.question.room.entity.remote
import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/15.
 */

data class QuestionEntity(
		@SerializedName("id") var id: Int? = 0,
		@SerializedName("content") var content: String? = "",
		@SerializedName("imageUrls") var imageUrls: String? = "",
		@SerializedName("replyCount") var replyCount: Int? = 0,
		@SerializedName("createTime") var postTime: String? = "",
		@SerializedName("user") var postUser: PostUser? = PostUser(),
		@SerializedName("imageHost") var imageHost: String? = "",
		@SerializedName("location") var location: String? = ""
)

data class PostUser(
		@SerializedName("headimgTemp") var headimg: String? = "",
		@SerializedName("realName") var username: String? = "",
		@SerializedName("sex") var sex: Int? = 0,
		@SerializedName("intro") var intro: String? = "",
		@SerializedName("plantingArea") var plantingArea: Int? = 0,
		@SerializedName("address") var address: String? = "",
		@SerializedName("career") var career: String? = "",
		@SerializedName("birthday") var birthday: String? = "",
		@SerializedName("age") var age: Int? = 0
)