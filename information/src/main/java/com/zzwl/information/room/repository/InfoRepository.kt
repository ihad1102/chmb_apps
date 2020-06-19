package com.zzwl.information.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.information.api.InformationApi

class InfoRepository : BaseRepository() {
    fun getInformation(pageSize:Int,pageNum:Int) = NetworkBoundResult({
        apiProvider.create(InformationApi::class.java).getInformation(pageSize,pageNum)
    })
    fun getInforMationDetails(id:Int)=NetworkBoundResult({
        apiProvider.create(InformationApi::class.java).getInformationDetails(id)
    })
}