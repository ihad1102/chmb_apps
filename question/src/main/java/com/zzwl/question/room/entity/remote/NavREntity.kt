package com.zzwl.question.room.entity.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by G on 2018/1/17 0017.
 */
data class NavREntity(
        @SerializedName("node") var node: String = "",
        @SerializedName("ext") var detail: Map<String, String> = HashMap()
)