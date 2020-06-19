package com.zzwl.bakeMedicine.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.information.room.entity.Notice
import com.zzwl.bakeMedicine.room.repository.NoticeRepository

class NoticeViewModel : BaseViewModel() {
    private val noticeRepository by lazy { NoticeRepository() }
    private val pageNumLiveData by lazy { MutableLiveData<Int>() }
    val noticeList by lazy { ArrayList<Notice>() }
    val limit = 10
    fun setNoticeLiveData(pageNum: Int) {
        pageNumLiveData.value = pageNum
    }

    fun obsNotice() = pageNumLiveData.switchMap {
        noticeRepository.getNotice(it, limit)
    }

}