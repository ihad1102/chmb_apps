package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class TemplateREntity(
        @SerializedName("cropId") var cropId: Int = 0,
        @SerializedName("cropName") var cropName: String = "",
        @SerializedName("cropArea") var cropArea: String = "",
        @SerializedName("annual") var annual: String = "",
        @SerializedName("principal") var principal: String = "",
        @SerializedName("phone") var phone: String = "",
        @SerializedName("blocks") var blocks: ArrayList<BlockCEntity> = arrayListOf()
)

@Entity
data class BlockCEntity(
        @PrimaryKey
        @SerializedName("blockId") var blockId: String = "",
        @SerializedName("districtId") var districtId: String = "",
        @SerializedName("blockName") var blockName: String = "",
        @SerializedName("blockArea") var blockArea: String = "",
        @SerializedName("address") var address: String = ""
)

@Entity
data class CropCEntity(
        @PrimaryKey
        @SerializedName("plantingId") var plantingId: String = "",
        @SerializedName("cropId") var cropId: String = "",
        @SerializedName("cropName") var cropName: String = "",
        @SerializedName("cropArea") var cropArea: String = "",
        @SerializedName("principal") var principal: String = "",
        @SerializedName("phone") var phone: String = "",
        @SerializedName("batch") var batch: String = "",
        @SerializedName("createTime") var createTime: String = "",
        @SerializedName("lock") var lock: Boolean = false,
        @Ignore @SerializedName("blocks") var blocks: ArrayList<BlockCEntity> = ArrayList()
)

@Entity
data class LogCEntity(
        @PrimaryKey
        @SerializedName("id") var id: String = "",
        @SerializedName("batch") var batch: String = "",
        @SerializedName("plantingType") var plantingType: String = "",
        @SerializedName("cropName") var cropName: String = "",
        @SerializedName("plantingDetail") var plantingDetail: String = "",
        @SerializedName("plantingImages") var plantingImages: String = "",
        @SerializedName("plantingTime") var plantingTime: Long = 0,
        @SerializedName("imageHost") var imageHost: String = "",
        @SerializedName("plantingId") var plantingId: String = "",
        @SerializedName("example") var example: Boolean = false
)

@Entity(
        primaryKeys = ["blockId", "plantingId"],
        foreignKeys = [
            (ForeignKey(entity = BlockCEntity::class, parentColumns = ["blockId"], childColumns = ["blockId"])),
            (ForeignKey(entity = CropCEntity::class, parentColumns = ["plantingId"], childColumns = ["plantingId"]))
        ])
data class CropBlockRelationLEntity(
        var plantingId: String = "",
        var blockId: String = ""
)
