package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by qhn on 2018/1/8.
 */

@Entity
data class CropTypeEntity(
        @PrimaryKey
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("name") var name: String? = "",
        @SerializedName("collectCount") var collectCount: Int? = 0,
        @SerializedName("sort") var sort: Int? = 0
)