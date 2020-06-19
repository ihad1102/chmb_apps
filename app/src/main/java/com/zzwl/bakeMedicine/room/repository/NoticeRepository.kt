package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.information.api.NoticeApi

class NoticeRepository : BaseRepository() {
    fun getNotice(pageNum: Int, pageSize: Int) = NetworkBoundResult({
        apiProvider.create(NoticeApi::class.java).getNotice(pageNum, pageSize)
    })
}