package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.entity.common.ExpertCEntity
import com.zzwl.question.room.repository.MyselfRepository

/**
 * Created by qhn on 2018/1/11.
 */
class MyFollowViewModel : BaseViewModel() {
    private val myselfRepository by lazy { MyselfRepository() }
    private val obsMyFollowExpert by lazy { MutableLiveData<Pair<Int, Int>>() }
    val expertLimit = 10
    val expertList by lazy { ArrayList<ExpertCEntity>() }
    fun obsMyFollowExpert() = obsMyFollowExpert
            .switchMap {
                myselfRepository.getMyFollowedExpert(it.first, it.second)
            }

    fun setObsExpertLiveData(offset: Int) {
        obsMyFollowExpert.value = Pair(offset, expertLimit)
    }


}