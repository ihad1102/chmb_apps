package com.zzwl.question.room.repository

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.g.base.appContent
import com.g.base.common.apiProvider
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.retryWithDelay
import com.g.base.extend.toast
import com.g.base.help.tryCatch
import com.g.base.room.repository.BaseRepository
import com.g.base.ui.BaseActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zzwl.question.api.GeocoderApi
import com.zzwl.question.room.entity.AddressComponent
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import kotlin.concurrent.thread

class LocationRepository : BaseRepository() {
    //callBack location为空表示无法获取定位权限
    @SuppressLint("CheckResult", "MissingPermission")
    fun getGpsLocation(activity: BaseActivity<*>, callBack: (Location?) -> Unit) {
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(
                        {
                            if (!it) callBack(null)
                            SmartLocation.with(activity).location(LocationManagerProvider()).oneFix().start {
                                callBack.invoke(it)
                            }
                        })
    }

    @SuppressLint("CheckResult")
    fun getGeocoder(lat: Double, lon: Double, callBack: (city: String, province: String, district: String) -> Unit) {
        apiProvider.create(GeocoderApi::class.java)
                .baiduGeocder("$lat,$lon")
                .retryWithDelay(3, 1000 * 10)
                .map {
                    if (it.status != 0) throw RuntimeException("")
                    val a = it.result.addressComponent
                    a.province = a.province.slice(0..a.province.length - 2)
                    a.city = a.city.slice(0..a.city.length - 2)
                    it
                }
                .mainIoSchedulers()
                .subscribe(
                        {
                            val a = it.result.addressComponent
                            callBack(a.province, a.city, a.district)
                        },
                        {
                            val a = AddressComponent()
                            callBack(a.province, a.city, a.district)
                        }
                )
    }

    //requestLocationUpdates
    @SuppressLint("CheckResult", "MissingPermission")
    fun getLocation(activity: BaseActivity<*>, locationListener: LocationListener, locationManager: LocationManager) {
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(
                        {
                            if (it) {
                                val provider: String? = when {
                                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                                        LocationManager.GPS_PROVIDER
                                    }
                                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                                        LocationManager.NETWORK_PROVIDER
                                    }
                                    else -> {
                                        null
                                    }
                                }
                                locationManager.requestLocationUpdates(provider, 1000 * 60 * 5, 20f, locationListener)
                            }

                        },
                        {
                            activity.toast("获取定位权限失败")
                        })
    }

    //根据经纬度反查地址
    fun getLocationAddressPegging(lat: Double, lon: Double, callBack: (city: String, province: String, district: String) -> Unit) {
        thread {
            tryCatch {
                val address = Geocoder(appContent).getFromLocation(lat, lon, 1)[0]
                if (address.adminArea != null && address.locality != null && address.subAdminArea != null) {
                    callBack(address.subAdminArea, address.adminArea, address.locality)
                }
            }
        }
    }

}