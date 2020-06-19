package com.zzwl.bakeMedicine.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.entity.remote.UserInfoEntity
import com.zzwl.bakeMedicine.room.repository.UserRepository

class PersonCenterViewModel : BaseViewModel() {
    lateinit var userInfoEntity: UserInfoEntity
    private val repository by lazy { UserRepository() }
    fun getUserInfo() = repository.getUserInfo()
}