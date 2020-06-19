package com.zzwl.bakeMedicine.room.entity.remote


data class TobaccoCurveEntity(

        val dryBulbGoalTemp: Float=0f,
        val wetBulbGoalTemp: Float=0f,
        val tempHoldingTime: Float=0f,
        val tempHeatingTime: Float=0f
)