package com.g.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.g.base.extend.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit


//定位
//照相机
//打电话
object RxPermissionsUtils {
    @SuppressLint("CheckResult", "MissingPermission")
    fun callPhone(activity: Activity, phone: String) {
        RxPermissions(activity)
                .request(Manifest.permission.CALL_PHONE)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(
                        {
                            activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
                        },
                        {
                            activity.toast("拨打电话权限被拒绝 请手动拨打 : $phone")
                        }
                )
    }

    @SuppressLint("CheckResult", "MissingPermission")
    fun getLocation(activity: Activity, onSuccess: () -> Unit, onFailed: (() -> Unit)? = null, onError: (() -> Unit)? = null) {
        RxPermissions(activity).request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(
                        {
                            if (it) {
                                onSuccess()
                            } else {
                                if (onFailed == null) {
                                    activity.toast("获取定位权限失败,  请前往设置页面授权.")
                                    DialogUtils.build(activity, msg = "获取定位权限失败,  请前往设置页面授权.", onPositive =
                                    {
                                        val packageURI = Uri.parse("package:" + activity.packageName)
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                                        activity.startActivity(intent)
                                    })

                                } else
                                    onFailed()
                            }
                        },
                        {
                            if (onError == null) {
                                activity.toast("获取定位权限失败,  请前往设置页面授权.")
                            } else
                                onError()
                        }
                )

    }

}