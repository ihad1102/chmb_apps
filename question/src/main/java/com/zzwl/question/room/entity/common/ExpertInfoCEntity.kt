package com.zzwl.question.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity
data class ExpertInfoCEntity(
        @PrimaryKey
        @SerializedName("id") var id: String = "",
        @SerializedName("headImage") var headImg: HeadImage = HeadImage(),
        @SerializedName("realName") var userName: String = "",
        @SerializedName("intro") var intro: String = "",
        @SerializedName("isFollow") var isFollow: Boolean = false,
        @SerializedName("career") var career: String = "",
        @SerializedName("adoptCount") var adoptCount: String = "0",
        @SerializedName("fansCount") var fansCount: String = "0",
        @SerializedName("score") var score: String = "0",
        @SerializedName("avg") var avg: String = "0",
        @SerializedName("crops") var crops: String = "葡萄",
        @SerializedName("userVerifiedCategoryTitle") var userVerifiedCategoryTitle: String = "首席专家",
        var createDate: Long = 0L
)