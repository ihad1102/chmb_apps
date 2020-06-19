package com.zzwl.information.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.information.api.NoticeApi

class WikiRepository : BaseRepository() {
    fun getWikiList(pageNum: Int, pageSize: Int, category: Int) = NetworkBoundResult(
            {
                apiProvider.create(NoticeApi::class.java).getWiki(pageNum, pageSize, category)
            })

    fun getWikiDetail(id: Int) = NetworkBoundResult({
        apiProvider.create(NoticeApi::class.java).getWikiDetail(id)
    })
}