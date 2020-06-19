package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.entity.common.ExpertInfoCEntity
import com.zzwl.question.room.repository.ExpertInfoRepository

class ExpertInfoViewModel : BaseViewModel() {
    private val expertInfoRepository by lazy { ExpertInfoRepository() }

    private val operateExpertInfo by lazy { MutableLiveData<String>() }
    fun operateExpertInfo(id: String) {
        operateExpertInfo.value = id
    }

    fun obsExpertInfo(force: Boolean = false) = operateExpertInfo.switchMap {
        expertInfoRepository.getExpertDetail(it, force)
    }

    fun followExpert(v: ExpertInfoCEntity) = expertInfoRepository.followExpert(v)

}