package com.zzwl.question.room.entity.common

import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/5.
 */


data class QuestionDetailEntity(
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("cropName") var cropName: String? = "",
        @SerializedName("content") var content: String? = "",
        @SerializedName("imageUrls") var subImages: String? = "",
        @SerializedName("replyCount") var replyCount: Int? = 0,
        @SerializedName("adoptReplyId") var adoptReplyId: Int? = 0,
        @SerializedName("postTime") var postTime: String? = "",
        @SerializedName("user") var postUser: PostUser? = PostUser(),
        @SerializedName("imageHost") var imageHost: String? = "",
        @SerializedName("replyList") var replys: List<Reply?>? = listOf()
)

data class Reply(
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("content") var content: String? = "",
        @SerializedName("agreeCount") var agreeCount: Int? = 0,
        @SerializedName("opposeCount") var opposeCount: Int? = 0,
        @SerializedName("createTime") var replyTime: String? = "",
        @SerializedName("imageUrls") var subImages: String? = "",
        @SerializedName("answerer") var replyUser: ReplyUser? = ReplyUser(),
        @SerializedName("commentList") var comments: List<Comment?>? = listOf(),
        @SerializedName("adopt") var adopt: Boolean? = false,
        @SerializedName("like") var like: Boolean? = null,
        @SerializedName("del") var del: Boolean = false,
        @SerializedName("imageList") var imageList: List<Image?>? = listOf()
)

data class ReplyUser(
        @SerializedName("id") var userId: Int = 0,
        @SerializedName("verified") var verified: Int = 0,
        @SerializedName("headImage") var headImg: HeadImage = HeadImage(),
        @SerializedName("realName") var userName: String? = "",
        @SerializedName("sex") var sex: Int? = 0
)

data class Comment(
        @SerializedName("content") var content: String? = "",
        @SerializedName("headImageUrl") var headImg: String? = "",
        @SerializedName("createTime") var commentTime: String? = "",
        @SerializedName("del") var del: Boolean = false,
        @SerializedName("user") var user: User = User()
)

data class PostUser(
        @SerializedName("headImage") var headImg: HeadImage? = HeadImage(),
        @SerializedName("realName") var userName: String? = "",
        @SerializedName("sex") var sex: Int? = 0,
        @SerializedName("intro") var intro: String? = "",
        @SerializedName("plantingArea") var plantingArea: Int? = 0,
        @SerializedName("address") var address: String? = "",
        @SerializedName("career") var career: String? = "",
        @SerializedName("birthday") var birthday: String? = "",
        @SerializedName("age") var age: Int? = 0
)

data class HeadImage(@SerializedName("url") var url: String? = "")
data class Image(@SerializedName("url") var url: String? = "")

data class User(@SerializedName("headImageUrl") var headImg: String? = "",
                @SerializedName("realName") var realName: String? = "")

