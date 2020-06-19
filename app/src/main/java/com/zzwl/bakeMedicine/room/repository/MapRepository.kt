package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.bakeMedicine.api.MapApi

class MapRepository : BaseRepository() {
    fun getMapList(id:Int) = NetworkBoundResult(
            {
                apiProvider.create(MapApi::class.java).getMapList(id)
            }
    )
}