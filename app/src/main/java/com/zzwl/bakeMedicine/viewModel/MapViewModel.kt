package com.zzwl.bakeMedicine.viewModel

import android.arch.lifecycle.ViewModel
import com.zzwl.bakeMedicine.room.entity.remote.MapEntity
import com.zzwl.bakeMedicine.room.repository.MapRepository
import com.zzwl.bakeMedicine.ui.map.MapDataSet

class MapViewModel :ViewModel() {
    private val mapRepository by lazy { MapRepository() }
    val mapDataList by lazy { ArrayList<MapDataSet>() }
    fun getMapList(id:Int) = mapRepository.getMapList(id)

}