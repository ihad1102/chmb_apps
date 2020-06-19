package com.zzwl.information.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.information.room.entity.WikiEntity
import com.zzwl.information.room.repository.WikiRepository

class WikiViewModel : BaseViewModel() {
    private val repository by lazy { WikiRepository() }
    val limit = 10
    private val wikiLiveData by lazy { MutableLiveData<Pair<Int, Int>>() }
    val wikiEntityList by lazy { ArrayList<WikiEntity>() }

    fun operateWikiList(pageNum: Int, category: Int) {
        wikiLiveData.value = Pair(pageNum, category)
    }

    fun obsWikiList() = wikiLiveData.switchMap { repository.getWikiList(it.first, limit, it.second) }

    fun getWikiDetail(id: Int) = repository.getWikiDetail(id)
}
