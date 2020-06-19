package com.zzwl.farmingtrade.room.entity.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AddressItemCEntity(
        @PrimaryKey
        @SerializedName("addressId") var addressId: String = "",
        @SerializedName("consignee") var consignee: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("province") var province: String = "",
        @SerializedName("district") var district: String = "",
        @SerializedName("city") var city: String = "",
        @SerializedName("provinceId") var provinceId: String = "",
        @SerializedName("districtId") var districtId: String = "",
        @SerializedName("address") var address: String = "",
        @SerializedName("cityId") var cityId: String = "",
        @SerializedName("pickupPointId") var pickupPoint: String = "",
        @SerializedName("isDefault") var isDefault: Boolean = false,
        var isSelect: Boolean = false
)