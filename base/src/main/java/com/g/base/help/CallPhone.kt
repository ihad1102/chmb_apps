package com.g.base.help

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.g.base.extend.toast
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit

/**
 * Created by G on 2017/9/5 0005.
 */
object CallPhone {
    @SuppressLint("CheckResult", "MissingPermission")
    fun callTel(activity: Activity, phone: String) {
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
}
