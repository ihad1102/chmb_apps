package com.zzwl.bakeMedicine.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.repository.HistoryCurveRepository

class HistoryCurveViewModel : BaseViewModel() {
    private val repository by lazy { HistoryCurveRepository() }
    private val dateLiveDate by lazy { MutableLiveData<Pair<String, String>>() }

    fun setDateLiveDate(dateFrom: String, dateString: String) {
        dateLiveDate.value = Pair(dateFrom, dateString)
    }

    fun getHistoryCurveData(bakeId: Int) =
            repository.getHistoryCurveData(bakeId)

    fun getPullDownData(houseId: Int) =
            repository.getHistoryPullDownData(houseId)


}