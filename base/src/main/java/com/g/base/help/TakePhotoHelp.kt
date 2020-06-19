package com.g.base.help

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.ObservableField
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.Gravity
import com.g.base.R
import com.g.base.databinding.DialogPhotoChoiceBinding
import com.g.base.extend.observeNullableEx
import com.g.base.ui.BaseActivity
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoImpl
import com.jph.takephoto.compress.CompressConfig
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.InvokeParam
import com.jph.takephoto.model.TContextWrap
import com.jph.takephoto.model.TakePhotoOptions
import com.jph.takephoto.permission.InvokeListener
import com.jph.takephoto.permission.PermissionManager
import com.jph.takephoto.permission.TakePhotoInvocationHandler
import java.io.File
import kotlin.math.max

/**
 * Created by G on 2017/10/20 0020.
 */
class TakePhotoHelp : Fragment() , InvokeListener {
    companion object {
        fun getInstant(activity: Activity, resultListener: TakePhoto.TakeResultListener
                       , maxSelect: ObservableField<Int> = ObservableField(6)
                       , cropOptions: CropOptions? = null): TakePhotoHelp {
            if (activity !is BaseActivity<*>) throw RuntimeException("只有继承于BackActivity的子类才可以使用")
            val instant = TakePhotoHelp().setupSetting(resultListener,maxSelect, cropOptions)
            activity.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.rootLayout,instant)
                    .commitAllowingStateLoss()
            return instant
        }

        fun showCameraPermissionRefuseDialog(activity: Activity,msg : String? = null){
            AlertDialog.Builder(activity)
                    .setTitle("允许拍照")
                    .setCancelable(true)
                    .setMessage(msg ?: "没有拍照的权限")
                    .setPositiveButton("去设置"){ _: DialogInterface, _: Int ->
                        val  localIntent =  Intent()
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        localIntent.data = Uri.fromParts("package", activity.packageName, null)
                        activity.startActivity(localIntent)
                    }
                    .setNegativeButton("算了,我拍照片了了"){ _: DialogInterface, i: Int -> }
                    .show()
        }
    }

    private val attachLiveData by lazy { MediatorLiveData<Boolean>() }
    private lateinit var resultListener : TakePhoto.TakeResultListener
    private lateinit var maxSelect: ObservableField<Int>
    private var cropOptions: CropOptions? = null

    private fun setupSetting(resultListener: TakePhoto.TakeResultListener,
                             maxSelect: ObservableField<Int>,
                             cropOptions: CropOptions?): TakePhotoHelp {
        this.resultListener = resultListener
        this.maxSelect = maxSelect
        this.cropOptions = cropOptions
        return this
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        attachLiveData.value = true
    }

    fun startPhotoChoice() {
        attachLiveData.observeNullableEx(this){
                    if (it != true) return@observeNullableEx
                    val bottomDialog = Dialog(activity, R.style.BottomDialog)
                    val contentView = DialogPhotoChoiceBinding.inflate(layoutInflater,null,false)

                    contentView.gallery.setOnClickListener { bottomDialog.dismiss(); startGallery() }
                    contentView.camera.setOnClickListener { bottomDialog.dismiss(); startCamera() }
                    contentView.close.setOnClickListener { bottomDialog.dismiss() }

                    bottomDialog.setContentView(contentView.root)
                    val layoutParams = contentView.root.layoutParams
                    layoutParams.width = resources.displayMetrics.widthPixels
                    contentView.root.layoutParams = layoutParams
                    bottomDialog.window.setGravity(Gravity.BOTTOM)
                    bottomDialog.setCanceledOnTouchOutside(true)
                    bottomDialog.window.setWindowAnimations(R.style.BottomDialog_Animation)
                    bottomDialog.show()
                }
    }

    //开始拍照
    fun startCamera() {
        attachLiveData.observeNullableEx(this) {
                    if (it != true) return@observeNullableEx
                    val file = File(Environment.getExternalStorageDirectory(), "/zhxn/farming/" + System.currentTimeMillis() + ".jpg")
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    val imageUri = Uri.fromFile(file)
                    val builder1 = TakePhotoOptions.Builder()

                    val takePhoto = getTakePhoto()
                    takePhoto.setTakePhotoOptions(builder1.create())
                    //压缩
                    takePhoto.onEnableCompress(CompressConfig.Builder()
                            .setMaxSize(512000) //500KB
                            .enableReserveRaw(false)
                            .create(), true)

                    if (cropOptions == null)
                        takePhoto.onPickFromCapture(imageUri)
                    else
                        takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions)
                }
    }

    //从相册选择
    fun startGallery() {
        attachLiveData.observeNullableEx(this){
                    if (it != true) return@observeNullableEx
                    val builder1 = TakePhotoOptions.Builder()
                    val takePhoto = getTakePhoto()
                    takePhoto.setTakePhotoOptions(builder1.create())
                    //压缩
                    takePhoto.onEnableCompress(CompressConfig.Builder()
                            .setMaxSize(512000) //500KB
                            .enableReserveRaw(false)
                            .create(), true)

                    if (cropOptions == null)
                        takePhoto.onPickMultiple(max(maxSelect.get() ?: 0, 0))
                    else
                        takePhoto.onPickMultipleWithCrop(max(maxSelect.get() ?: 0, 0), cropOptions)
                }
    }

    /**
     * 获取TakePhoto实例
     * @return
     */
    private fun getTakePhoto(): TakePhoto {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, resultListener)) as TakePhoto
        }
        return takePhoto!!
    }


    //实现即可
    private var invokeParam: InvokeParam? = null
    private var takePhoto: TakePhoto? = null

    override fun onSaveInstanceState(outState: Bundle) {
        getTakePhoto().onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(activity, type, invokeParam, resultListener)
    }

    override operator fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.method)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }
}