package com.zzwl.farmingtrade.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zzwl.farmingtrade.room.entity.common.AddressItemCEntity
import com.zzwl.farmingtrade.room.entity.common.AddressRegionCEntity

/**
 * Created by G on 2017/11/16 0016.
 */
@Dao
interface AddressDao {
    companion object {
        const val ADDRESS_TABLE_NAME = "AddressItemCEntity"
        const val REGION_TABLE_NAME = "AddressRegionCEntity"
    }

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME")
    fun getAll(): LiveData<List<AddressItemCEntity>>

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME")
    fun getAllSync(): List<AddressItemCEntity>

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME WHERE isSelect = 1 OR isDefault = 1 ORDER BY isSelect DESC LIMIT 1")
    fun getSelect(): LiveData<AddressItemCEntity>

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME WHERE isSelect = 1 OR isDefault = 1 ORDER BY isSelect DESC LIMIT 1")
    fun getSelectSync(): AddressItemCEntity?

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME WHERE isDefault = 1 LIMIT 1")
    fun getDefault(): LiveData<AddressItemCEntity>

    @Query("UPDATE $ADDRESS_TABLE_NAME SET isSelect = 0 WHERE isSelect = 1")
    fun cleanSelect()

    @Query("DELETE FROM $ADDRESS_TABLE_NAME")
    fun nukeAddressTable()

    @Query("DELETE FROM $REGION_TABLE_NAME")
    fun nukeRegionTable()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(v: AddressItemCEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAddress(vararg v: AddressItemCEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRegion(vararg v: AddressRegionCEntity)

    @Query("SELECT * FROM $REGION_TABLE_NAME WHERE type = :type AND parent = :parent")
    fun getRegions(type: String, parent: String): LiveData<List<AddressRegionCEntity>>
}