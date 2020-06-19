package com.zzwl.farmingtrade.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zzwl.farmingtrade.room.entity.common.BlockCEntity
import com.zzwl.farmingtrade.room.entity.common.CropBlockRelationLEntity
import com.zzwl.farmingtrade.room.entity.common.CropCEntity
import com.zzwl.farmingtrade.room.entity.common.LogCEntity

@Dao
interface FarmingDao {
    companion object {
        const val BlockCEntityTable = "BlockCEntity"
        const val CropCEntityTable = "CropCEntity"
        const val LogCEntityTable = "LogCEntity"
        const val CropBlockRelationLEntityTable = "CropBlockRelationLEntity"
    }

    //获取全部地块
    @Query("SELECT * FROM $BlockCEntityTable")
    fun getAllBlock(): LiveData<List<BlockCEntity>?>

    //获取指定作物的地块
    @Query("""SELECT * FROM $BlockCEntityTable INNER JOIN $CropBlockRelationLEntityTable ON
            $BlockCEntityTable.blockId = $CropBlockRelationLEntityTable.blockId WHERE
           $CropBlockRelationLEntityTable.plantingId=:plantingId""")
    fun getCropBlock(plantingId: String): LiveData<List<BlockCEntity>?>

    //获取指定作物的地块
    @Query("""SELECT * FROM $BlockCEntityTable INNER JOIN $CropBlockRelationLEntityTable ON
            $BlockCEntityTable.blockId = $CropBlockRelationLEntityTable.blockId WHERE
           $CropBlockRelationLEntityTable.plantingId=:plantingId""")
    fun getCropBlockSync(plantingId: String): List<BlockCEntity>?

    //添加地块
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlock(vararg blockCEntity: BlockCEntity)

    //添加作物地块的关系
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCropBlock(vararg cropBlockRelationLEntity: CropBlockRelationLEntity)

    //获取全部记录
    @Query("SELECT * FROM $LogCEntityTable ORDER BY plantingTime DESC LIMIT :limit")
    fun getAllLog(limit: Int): LiveData<List<LogCEntity>?>

    //获取指定作物的记录
    @Query("SELECT * FROM $LogCEntityTable WHERE plantingId=:plantingId ORDER BY id DESC LIMIT :limit")
    fun getCropLog(plantingId: String, limit: Int): LiveData<List<LogCEntity>?>

    //删除示例
    @Query("DELETE FROM $LogCEntityTable WHERE example=1")
    fun nukeLogExample()

    //删除非示例
    @Query("DELETE FROM $LogCEntityTable WHERE example=1")
    fun nukeLogNotExample()

    //删除记录
    @Query("DELETE FROM $LogCEntityTable")
    fun nukeLog()

    //添加记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(vararg logCEntity: LogCEntity)

    //删除记录
    @Delete()
    fun deleteLog(logCEntity: LogCEntity)

    //获取全部作物
    @Query("SELECT * FROM $CropCEntityTable  WHERE plantingId = :plantingId")
    fun getCropById(plantingId: String?): LiveData<List<CropCEntity>?>

    //获取全部作物
    @Query("SELECT * FROM $CropCEntityTable")
    fun getAllCrop(): LiveData<List<CropCEntity>?>

    //获取全部作物
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrop(vararg cropCEntity: CropCEntity)

    //删除指定作物
    @Delete()
    fun deleteCrop(cropCEntity: CropCEntity)
}