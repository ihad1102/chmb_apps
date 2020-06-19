package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by G on 2017/11/16 0016.
 */
@Entity
data class AddressRegionCEntity(
        @PrimaryKey
        @SerializedName("regionId") var regionId: String = "",
        @SerializedName("regionName") var regionName: String? = null,
        var type: String? = null,
        var parent: String? = null
)