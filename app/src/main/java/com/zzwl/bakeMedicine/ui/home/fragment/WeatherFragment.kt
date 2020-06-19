package com.zzwl.bakeMedicine.ui.home.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.view.View
import com.g.base.extend.toast
import com.g.base.ui.BaseFragment
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.FragmentWeatherBinding
import com.g.base.utils.RxPermissionsUtils
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel
import com.zzwl.bakeMedicine.viewModel.WeatherData
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now
import interfaces.heweather.com.interfacesmodule.view.HeWeather
import java.text.SimpleDateFormat

class WeatherFragment : BaseFragment<FragmentWeatherBinding>() {
    val weatherObs by lazy { ObservableField<String>("无") }
    val cityObs by lazy { ObservableField<String>() }
    val tmpObs by lazy { ObservableField<String>("0℃") }
    val weekObs by lazy { ObservableField<String>() }
    val dateObs by lazy { ObservableField<String>() }
    val pressureObs by lazy { ObservableField<String>() }
    val RHObs by lazy { ObservableField<String>() }
    val visibilityObs by lazy { ObservableField<String>() }
    val windObs by lazy { ObservableField<String>() }
    val bgObs by lazy { ObservableField<Int>(R.drawable.ic_sunny_day_bg) }
    val imgWeatherObs by lazy { ObservableField<Int>(R.drawable.ic_sunny_day) }
    val viewModel by lazy { ViewModelProviders.of(activity!!).get(TobaccoStatisticsViewModel::class.java) }

    override fun setContentView(): Int = R.layout.fragment_weather
    @SuppressLint("SimpleDateFormat")
    override fun onLazyLoadOnce() {
        contentView.data = this
        showContentView()
        getWeather()
        contentView.imgReload.setOnClickListener {
            getWeather()
        }
    }

    @SuppressLint("CheckResult")
    private fun getWeather() {
        RxPermissionsUtils.getLocation(activity!!,{
            getWeatherAfterPermission()
        })
    }

    private fun getWeatherAfterPermission() {
        HeWeather.getWeatherNow(context, object : HeWeather.OnResultWeatherNowBeanListener {
            override fun onSuccess(p0: MutableList<Now>?) {
                if (p0?.isNotEmpty() == true)
                    setData(WeatherData(p0[0].now.cond_code,
                            p0[0].basic.parent_city,
                            p0[0].now.cond_txt,
                            p0[0].now.tmp,
                            p0[0].now.wind_dir + " " + p0[0].now.wind_sc + "级",
                            p0[0].now.pres,
                            p0[0].now.hum + "%RH",
                            p0[0].now.vis + "km")
                    )
            }

            override fun onError(p0: Throwable?) {
                if (contentView.imgReload.visibility == View.GONE) {
                    weekObs.set("")
                    dateObs.set("")
                    contentView.imgReload.visibility = View.VISIBLE
                }

                toast("获取天气失败")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun setData(weatherData: WeatherData) {
        if (contentView.imgReload.visibility == View.VISIBLE)
            contentView.imgReload.visibility = View.GONE
        val currentTimeMillis = System.currentTimeMillis()
        weekObs.set(SimpleDateFormat("EEEE").format(currentTimeMillis))
        dateObs.set(SimpleDateFormat("yyyy-MM-dd").format(currentTimeMillis))
        pressureObs.set(weatherData.pressure)
        windObs.set(weatherData.wind)
        visibilityObs.set(weatherData.visibility)
        RHObs.set(weatherData.RH)
        weatherObs.set(weatherData.condText)
        cityObs.set(weatherData.city)
        tmpObs.set(weatherData.tmp + "℃")
        if (weatherData.condCode.isNotEmpty()) {
            when (weatherData.condCode) {
                "100" -> {//晴
                    bgObs.set(R.drawable.ic_sunny_day_bg)
                    imgWeatherObs.set(R.drawable.ic_sunny_day)
                }
                "101", "103" -> {//多云
                    bgObs.set(R.drawable.ic_cloudy_bg)
                    imgWeatherObs.set(R.drawable.ic_cloudy)
                }
                "104" -> {//阴
                    bgObs.set(R.drawable.ic_overcast_bg)
                    imgWeatherObs.set(R.drawable.ic_overcast)
                }
                else -> {
                    when (weatherData.condCode[0].toString()) {
                        "3" -> {//雨
                            bgObs.set(R.drawable.ic_rain_bg)
                            imgWeatherObs.set(R.drawable.ic_rain)
                        }
                        "4" -> {//雪
                            bgObs.set(R.drawable.ic_snow_bg)
                            imgWeatherObs.set(R.drawable.ic_snow)
                        }
                    }
                }
            }
        }
    }


}