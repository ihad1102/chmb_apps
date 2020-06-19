package com.zzwl.information.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.information.room.entity.InfoEntity
import com.zzwl.information.room.repository.InfoRepository

class InformationViewModel : BaseViewModel() {
    private val repository by lazy { InfoRepository() }
    val limit = 10
    val informationList by lazy { ArrayList<InfoEntity>() }
    private val pageNumLiveData by lazy { MutableLiveData<Int>() }

    fun setInformationPageNum(pageNum: Int) {
        pageNumLiveData.value = pageNum
    }

    fun obsInformationPage() = pageNumLiveData.switchMap {
        repository.getInformation(limit, it)
    }

    fun getInfomationDetails(id: Int) = repository.getInforMationDetails(id)
}