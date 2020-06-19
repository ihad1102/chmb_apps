package com.zzwl.bakeMedicine.room.entity.remote

import com.google.gson.annotations.SerializedName
import com.zzwl.bakeMedicine.room.entity.common.Image


data class MyAnswerREntity(
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("content") var content: String? = "",
        @SerializedName("imageUrls") var subImages: String? = "",
        @SerializedName("createTime") var replyTime: String? = "",
        @SerializedName("question") var question: Question? = Question(),
        @SerializedName("imageList") var imageList: List<Image?>? = listOf()
)

data class Question(
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("content") var content: String? = "",
        @SerializedName("imageUrls") var subImages: String? = "",
        @SerializedName("user") var postUser: PostedUser? = PostedUser(),
        @SerializedName("imageHost") var imageHost: String? = "",
        @SerializedName("imageUrlList") var imageUrlList: List<String?>? = listOf()
)

data class PostedUser(
        @SerializedName("realName") var userName: String? = "",
        @SerializedName("sex") var sex: Int? = 0
)