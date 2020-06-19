package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.entity.common.ExpertCEntity
import com.zzwl.question.room.repository.ExpertRepository

/**
 * Created by qhn on 2018/1/3.
 */
class ExpertViewModel : BaseViewModel() {
    private val expertRepository by lazy { ExpertRepository() }
    private val operateExpert by lazy { MutableLiveData<HashMap<String, String?>>() }
    private val searchExpertLivedata by lazy { MutableLiveData<Map<String, String>>() }
    private val locationLiveData by lazy { MutableLiveData<Pair<String, String>>() }
    val searchExpertList by lazy { ArrayList<ExpertCEntity>() }
    val limit = 10


    fun operateExpertList(offset: Int, isFollow: Boolean?, categoryId: String? = null, regionId: String? = null, keyword: String? = null, cropId: String? = null) {
        val pageNum = if (offset / limit == 0) 1 else offset / limit + 1

        val map = HashMap<String, String?>()
        map["pageNum"] = pageNum.toString()
        map["pageSize"] = limit.toString()
        map["isFollow"] = isFollow.toString()

        operateExpert.value = map

    }

    fun obsExpertList() = operateExpert.switchMap { expertRepository.getExpertList(it, it["isFollow"]!!.toBoolean()) }

    fun operateSearchExpert(pageNum: Int, keyword: String) {
        searchExpertLivedata.value = mapOf(
                Pair("pageNum", pageNum.toString()),
                Pair("pageSize", limit.toString()),
                Pair("keyword", keyword))
    }

    fun obsSearchExpertList() =
            searchExpertLivedata.switchMap {
                expertRepository.searchExpert(it["keyword"]!!, it["pageSize"]!!, it["pageNum"]!!)
            }

    private fun getExpertType() =
            expertRepository.getExpertType()



}

