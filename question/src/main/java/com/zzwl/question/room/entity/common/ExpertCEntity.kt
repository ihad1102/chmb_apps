package com.zzwl.question.room.entity.common

import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/3.
 */


data class ExpertCEntity(
        @SerializedName("id") var id: String = "",
        @SerializedName("headImage") var headImg: HeadImage = HeadImage(),
        @SerializedName("realName") var userName: String = "",
        @SerializedName("intro") var intro: String = "",
        var isFollow: Boolean = false
)