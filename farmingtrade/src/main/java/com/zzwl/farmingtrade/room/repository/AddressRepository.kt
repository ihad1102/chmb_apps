package com.zzwl.farmingtrade.room.repository

import com.g.base.api.ErrorCode
import com.g.base.common.apiProvider
import com.g.base.extend.runDataBaseTransition
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.farmingtrade.api.AddressApi
import com.zzwl.farmingtrade.room.database.AppDatabase
import com.zzwl.farmingtrade.room.entity.common.AddressItemCEntity

/**
 * Created by G on 2017/11/13 0013.
 */
class AddressRepository : BaseRepository() {
    fun getRegion(type: String, parent: String) =
            NetworkBoundResult(
                    {
                        apiProvider.create(AddressApi::class.java)
                                .getRegion(type, parent)
                    },
                    {
                        AppDatabase.instant.getAddressDao()
                                .getRegions(type, parent)
                    },
                    saveRemoteResult = {
                        it?.apply {
                            forEach { it.parent = parent; it.type = type }
                            AppDatabase.instant.runDataBaseTransition {
                                getAddressDao().insertAllRegion(*it.toTypedArray())
                            }
                        }
                    }
            )

    fun getPickupPoint(parent: String) = NetworkBoundResult(
            {
                apiProvider.create(AddressApi::class.java)
                        .getPickupPoint(parent)
            }
    )

    fun getAllAddress(refresh: Boolean): NetworkBoundResult<List<AddressItemCEntity>> {
        var selectId: String? = null
        return NetworkBoundResult(
                {
                    apiProvider.create(AddressApi::class.java)
                            .getAllAddress()
                },
                {
                    AppDatabase.instant.getAddressDao()
                            .getAll()
                },
                {
                    selectId = null
                    if (refresh) {
                        AppDatabase.instant.runDataBaseTransition {
                            selectId = getAddressDao().getAllSync().firstOrNull { it.isSelect }?.addressId
                            getAddressDao().nukeAddressTable()
                        }
                    }
                    refresh || it == null || it.isEmpty()
                },
                {
                    it?.apply {
                        AppDatabase.instant.runDataBaseTransition {
                            it.firstOrNull { it.addressId == selectId }?.isSelect = true
                            getAddressDao().insertAllAddress(*it.toTypedArray())
                        }
                    }
                },
                onRemoteFailed = {
                    if (it.code == ErrorCode.EMPTY) {
                        AppDatabase.instant.runDataBaseTransition {
                            getAddressDao().nukeAddressTable()
                        }
                    }
                }
        )

    }

    fun getSelect() =
            NetworkBoundResult(
                    loadFormDb = {
                        AppDatabase.instant
                                .getAddressDao()
                                .getSelect()
                    }
            )

    fun getDefault() =
            NetworkBoundResult(
                    loadFormDb = {
                        AppDatabase.instant
                                .getAddressDao()
                                .getDefault()
                    }
            )
}