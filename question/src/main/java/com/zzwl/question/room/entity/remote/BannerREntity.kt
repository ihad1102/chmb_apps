package com.zzwl.question.room.entity.remote

import com.google.gson.annotations.SerializedName


data class BannerREntity(
        @SerializedName("id") var id: Int = 0, //1
        @SerializedName("imageUrl") var imageUrl: String = "", //product/100023/f5b275ad-31c7-4cd8-8c95-479587db5dfb.jpg
        @SerializedName("htmlUrl") var htmlUrl: String = "", //www.baidu.com
        @SerializedName("imageHost") var imageHost: String = "" //http://zzwl0zsxn.oss-cn-hangzhou.aliyuncs.com/
)