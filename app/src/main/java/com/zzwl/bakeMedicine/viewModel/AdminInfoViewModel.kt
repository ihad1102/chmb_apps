package com.zzwl.bakeMedicine.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.entity.remote.AdminInfoEntity
import com.zzwl.bakeMedicine.room.entity.remote.Role
import com.zzwl.bakeMedicine.room.repository.AdminInfoRepository

class AdminInfoViewModel : BaseViewModel() {
    private val repository by lazy { AdminInfoRepository() }
    lateinit var adminInfoEntity: AdminInfoEntity
    val leaderList by lazy { ArrayList<Role>() }
    val adminList by lazy { ArrayList<Role>() }
    fun getAdminInfo(groupId: Int) =
            repository.getAdminInfo(groupId)
}