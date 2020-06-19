package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.zzwl.farmingtrade.room.entity.typeConverters.FertilizerListConvertes


@TypeConverters(FertilizerListConvertes::class)
@Entity
data class FertilizerListCEntity(
        @PrimaryKey
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("children") var children: List<Children> = listOf()
)

data class Children(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = ""
)