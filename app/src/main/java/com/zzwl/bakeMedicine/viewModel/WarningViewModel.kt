package com.zzwl.bakeMedicine.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.entity.remote.CurrentWarningEntity
import com.zzwl.bakeMedicine.room.entity.remote.HistoryWarningEntity

import com.zzwl.bakeMedicine.room.repository.WarningRepository

class WarningViewModel : BaseViewModel() {
    private val warningRepository by lazy { WarningRepository() }
    val limit = 20
    private val currentWaringLiveData by lazy { MutableLiveData<CurrentParams>() }
    private val historyWaringLiveData by lazy { MutableLiveData<HistoryParams>() }

    val currentWarningList by lazy { ArrayList<CurrentWarningEntity>() }
    val historyWarningList by lazy { ArrayList<HistoryWarningEntity>() }
    fun setCurrentWarning(houseId: String?, pageNum: Int, keyword: String?, houseGroupId: String) {
        currentWaringLiveData.value = CurrentParams(pageNum.toString(), limit.toString(), keyword, houseId, houseGroupId)
    }

    fun obsCurrentWarning() = currentWaringLiveData.switchMap {
        warningRepository.getCurrentWarning(it.houseId, it.pageNum, it.pageSize, it.keyword, it.houseGroupId)
    }

    fun setHistoryWarning(houseId: String?,
                          pageNum: Int,
                          beginTime: String,
                          endTime: String,
                          houseGroupId: String,
                          keyword: String?) {
        historyWaringLiveData.value = HistoryParams(pageNum.toString(), limit.toString(), beginTime, endTime, houseId, keyword, houseGroupId)
    }

    fun obsHistoryWarning() = historyWaringLiveData.switchMap {
        warningRepository.getHistoryWarning(it.houseId, it.pageNum, it.pageSize, it.beginTime, it.endTime, it.houseGroupId, it.keyword)
    }
}

data class HistoryParams(val pageNum: String,
                         val pageSize: String,
                         val beginTime: String,
                         val endTime: String,
                         val houseId: String?,
                         val keyword: String?,
                         val houseGroupId: String)

data class CurrentParams(val pageNum: String,
                         val pageSize: String,
                         val keyword: String?,
                         val houseId: String?,
                         val houseGroupId: String)