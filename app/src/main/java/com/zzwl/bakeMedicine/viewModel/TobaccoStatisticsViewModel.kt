package com.zzwl.bakeMedicine.viewModel

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.g.base.api.ErrorException
import com.g.base.extend.resultNotNull
import com.g.base.extend.switchMap
import com.g.base.extend.toObservable
import com.g.base.room.repository.ResultWarp
import com.g.base.room.repository.Status
import com.g.base.viewModel.BaseViewModel
import com.zzwl.bakeMedicine.room.entity.remote.TobaccoListInfoEntity
import com.zzwl.bakeMedicine.room.repository.TobaccoStatisticsRepository
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class TobaccoStatisticsViewModel : BaseViewModel() {
    private val repository by lazy { TobaccoStatisticsRepository() }
    private val houseIdLiveData by lazy { MutableLiveData<Int>() }
    private val hotPumpHouseIdLiveData by lazy { MutableLiveData<Int>() }
    private val groupIdLiveData by lazy { MutableLiveData<Int>() }

    lateinit var tobaccoListInfoEntity: TobaccoListInfoEntity


    var isRefreshAll = true//是否刷新全部三个holder


    //烤房状态
    fun setHouseLiveData(houseId: Int) {
        houseIdLiveData.value = houseId
    }

    //热泵烤房状态
    fun setHotPumpHouseLiveData(houseId: Int) {
        hotPumpHouseIdLiveData.value = houseId
    }

    //烤房列表
    fun setGroupLiveData(groupId: Int) {
        groupIdLiveData.value = groupId
    }

    fun getTobaccoStatistics() = groupIdLiveData.switchMap {
        repository.getTobaccoStatistics(it)
    }

    fun getTobaccoStatus() = houseIdLiveData.switchMap { repository.getTobaccoStatus(it) }

    fun getHotPumpTobaccoStatus() = hotPumpHouseIdLiveData.switchMap { repository.getHotPumpTobaccoStatus(it) }

    fun getTobaccoCurve(houseId: Int, leafOption: Int) = repository.getTobaccoCurve(houseId, leafOption)
}


data class WeatherData(var condCode: String, val city: String, val condText: String, val tmp: String,val wind:String,val pressure:String,val RH:String,val visibility:String)
