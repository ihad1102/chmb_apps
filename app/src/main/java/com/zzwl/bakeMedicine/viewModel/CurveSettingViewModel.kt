package com.zzwl.bakeMedicine.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.entity.remote.CurveEntity
import com.zzwl.bakeMedicine.room.repository.CurveSettingRepository

class CurveSettingViewModel : BaseViewModel() {
    private val repository by lazy { CurveSettingRepository() }
    val curveEntityList by lazy { ArrayList<CurveEntity>() }
    var curveEntityListCopy= ArrayList<CurveEntity>()
    fun getAllCurve(houseId: Int) = repository.getAllCurve(houseId)
}