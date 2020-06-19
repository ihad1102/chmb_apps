package com.zzwl.bakeMedicine.room.entity.remote


data class MapEntity(
        val id: Int,
        val name: String,
        val regionId: Int,
        val regionAddress: String,
        val addressDetail: String,
        val addressX: Double,
        val addressY: Double,
        val houseList: List<House>,
        val normalCount: Int,
        val alarmCount: Int,
        val fuelType: Int,
        val stopCount: Int
)

data class House(
        val id: Int,
        val name: String
)