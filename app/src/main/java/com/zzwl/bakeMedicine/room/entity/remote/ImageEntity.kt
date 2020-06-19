package com.zzwl.bakeMedicine.room.entity.remote
import com.google.gson.annotations.SerializedName



data class ImageEntity(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("url") var url: String = ""
)