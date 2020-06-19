package com.zzwl.farmingtrade.viewModel

import com.g.base.viewModel.BaseViewModel
import com.zzwl.farmingtrade.room.repository.CropRepository

/**
 * Created by qhn on 2018/1/8.
 */
class CropViewModel : BaseViewModel() {
    private val cropRepository by lazy { CropRepository() }
    fun getCrop(categoryId: Int, force: Boolean = true) = cropRepository.getCrops(categoryId, force)
    fun getCropType() = cropRepository.getCropType()
}