package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.repository.BasicInfoRepository
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

/**
 * Created by qhn on 2017/11/14.
 */
class BasicInfoViewModel : BaseViewModel() {
    private val getUserInfoOperate by lazy { MutableLiveData<Boolean>() }
    private val basicRepository by lazy { BasicInfoRepository() }


    fun operateUserInfo(force: Boolean) {
        getUserInfoOperate.value = force
    }

    //获取个人信息
    fun obsUserInfo() = getUserInfoOperate.switchMap {
        basicRepository.getUserInfo(it)
    }


    private fun convertToRequestBody(param: File): RequestBody =
            RequestBody.create(MediaType.parse("image/jpg"), param)

}

