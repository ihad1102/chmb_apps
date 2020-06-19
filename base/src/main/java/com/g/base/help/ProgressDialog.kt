package com.g.base.help

import android.annotation.SuppressLint
import android.app.Activity
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.g.base.ActivityLifecycleHelper
import com.g.base.R
import com.g.base.appContent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by G on 2017/11/6 0006.
 */
class ProgressDialog : CountDownTimer(800000L, 800L) {
    var dialog: SweetAlertDialog? = null
    var activity: Activity? = null

    var isLoading = false
    //定时器
    private var colorIndex = 0
    private val resources = appContent.resources
    override fun onTick(millisUntilFinished: Long) {
        colorIndex++
        dialog?.apply {
            when (colorIndex % 7) {
                0 -> progressHelper.barColor = resources.getColor(R.color.blue_btn_bg_color)
                1 -> progressHelper.barColor = resources.getColor(R.color.material_deep_teal_50)
                2 -> progressHelper.barColor = resources.getColor(R.color.success_stroke_color)
                3 -> progressHelper.barColor = resources.getColor(R.color.material_deep_teal_20)
                4 -> progressHelper.barColor = resources.getColor(R.color.material_blue_grey_80)
                5 -> progressHelper.barColor = resources.getColor(R.color.warning_stroke_color)
                6 -> progressHelper.barColor = resources.getColor(R.color.success_stroke_color)
            }
        }
    }

    override fun onFinish() {

    }

    fun setStart(msg: String, cancelAble: Boolean = false) {
        isLoading = true
        val latestActivity = ActivityLifecycleHelper.getLatestActivity()
        if (latestActivity !== activity) {
            tryCatch { dialog?.dismiss() }
            dialog = SweetAlertDialog(latestActivity)
        }
        dialog?.apply {
            setOnDismissListener {
                dialog = null
                activity = null
            }
            changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
            titleText = "Doing...!"
            contentText = msg
            setCancelable(cancelAble)
            if (!isShowing) {
                show()
            }
        }
        activity = latestActivity
        start()
    }

    @SuppressLint("CheckResult")
    fun setFiled(msg: String, exec: (ProgressDialog) -> Unit) {
        cancel()
        isLoading = false
        colorIndex = -1
        Observable.create<Boolean> { result ->
            dialog?.apply {
                titleText = "Failed~"
                contentText = msg
                confirmText = "确定"
                setConfirmClickListener { result.onNext(true); result.onComplete(); }
                changeAlertType(SweetAlertDialog.ERROR_TYPE)
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            exec(this)
                        },
                        {
                            exec(this)
                        }
                )
    }

    @SuppressLint("CheckResult")
    fun setSucceed(msg: String, exec: (ProgressDialog) -> Unit) {
        cancel()
        isLoading = false
        colorIndex = -1
        Observable.create<Boolean> { result ->
            dialog?.apply {
                titleText = "Succeed~"
                contentText = msg
                confirmText = "确定"
                setConfirmClickListener { result.onNext(true); result.onComplete(); }
                changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            }
        }.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            exec(this)
                        },
                        {
                            exec(this)
                        }
                )
    }

    fun dismiss(animation: Boolean = false) {
        cancel()
        colorIndex = -1
        tryCatch {
            if (animation)
                dialog?.dismissWithAnimation()
            else
                dialog?.dismiss()
        }
    }
}
