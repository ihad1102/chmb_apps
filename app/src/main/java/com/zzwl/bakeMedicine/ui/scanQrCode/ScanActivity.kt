package com.zzwl.bakeMedicine.ui.scanQrCode

import android.annotation.SuppressLint
import android.databinding.ObservableField
import android.os.Bundle
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.toast
import com.g.base.help.TakePhotoHelp
import com.g.base.help.tryCatch
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.TResult
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityScanBinding
import com.zzwl.bakeMedicine.event.ScanResultEvent
import com.zzwl.bakeMedicine.router.RouterPage
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import org.greenrobot.eventbus.EventBus

@Route(path = RouterPage.ScanActivity, extras = RouteExtras.NeedOauth)
class ScanActivity : BaseActivity<ActivityScanBinding>(), QRCodeView.Delegate {


    override var isFullScreen: Boolean = true
    val isSelectedBgObs by lazy { ObservableField(false) }


    override fun onScanQRCodeSuccess(result: String?) {
        if (result != null) {
            EventBus.getDefault().post(ScanResultEvent(result))
            finish()
        } else
            toast("二维码错误")
    }


    override fun onScanQRCodeOpenCameraError() {
        toast("打开相机出错了")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        contentView.data = this
        initView()
        showContentView()
    }

    private fun initView() {
        contentView.zxingView.startSpot()
        contentView.zxingView.setDelegate(this)
        contentView.imgBack.setOnClickListener {
            finish()
        }
        contentView.imgGallery.setOnClickListener {
            startGallery()
        }
        contentView.imgLight.setOnClickListener {
            if (isSelectedBgObs.get()!!) {
                contentView.zxingView.closeFlashlight()
                isSelectedBgObs.set(false)
            } else {
                contentView.zxingView.openFlashlight()
                isSelectedBgObs.set(true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        contentView.zxingView.startCamera()
        contentView.zxingView.showScanRect()

    }

    override fun onStop() {
        contentView.zxingView.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        contentView.zxingView.onDestroy()
        super.onDestroy()
    }


    private fun startGallery() {
        val maxSelect: ObservableField<Int> = ObservableField(1)
        val instant = TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeCancel() {
            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("出现异常,请重新选取图片")

            }

            override fun takeSuccess(result: TResult) {
                tryCatch {
                    scanPicture(result.images[0].originalPath)
                }
            }
        }, maxSelect)
        instant.startGallery()
    }

    @SuppressLint("CheckResult")
    private fun scanPicture(picturePath: String) {
        val scanPicObs = Observable.create(ObservableOnSubscribe<String> { e ->
            e.onNext(QRCodeDecoder.syncDecodeQRCode(picturePath))
            e.onComplete()
        })
        scanPicObs.mainIoSchedulers()
                .subscribe(
                        {
                            EventBus.getDefault().post(ScanResultEvent(it))
                            finish()
                        },
                        {
                            toast("未发现二维码")
                        })
    }
}