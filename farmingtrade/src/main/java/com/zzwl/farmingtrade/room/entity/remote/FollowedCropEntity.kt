package com.zzwl.farmingtrade.room.entity.remote

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 * Created by qhn on 2018/1/15.
 */
@Entity
data class FollowedCropEntity(
        @PrimaryKey
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("name") var name: String? = "",
        @SerializedName("imgimage") var imgimage: String? = "",
        @SerializedName("check") var check: Boolean? = false,
        @SerializedName("imageHost") var imageHost: String? = "",
        @SerializedName("sort") var sort: Int = 0,
        var categoryId: Int? = 0
)