package com.zzwl.bakeMedicine.ui.user

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.help.ProgressDialog
import com.g.base.help.TakePhotoHelp
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.TResult
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.api.UploadApi
import com.zzwl.bakeMedicine.databinding.FragmentRegister2Binding
import com.zzwl.bakeMedicine.room.repository.UserRepository
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.viewModel.RegisterViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class Register2Fragment : BaseFragment<FragmentRegister2Binding>(), TakePhoto.TakeResultListener {


    override fun setContentView(): Int = R.layout.fragment_register2
    private val faceImgMaxSelect = ObservableField(1)
    private val backImgMaxSelect = ObservableField(1)
    private val handImgMaxSelect = ObservableField(1)
    private var faceImgCheckedPhoto = ArrayList<String>()
    private var backImgCheckedPhoto = ArrayList<String>()
    private var handImgCheckedPhoto = ArrayList<String>()
    private var selectedType = 0 //0,1,2:选择身份证正面,选择身份证反面,选择手持身份证
    private val cropOptions: CropOptions = CropOptions.Builder().setAspectX(86).setAspectY(54).setWithOwnCrop(true).create()
    private val faceImgTakePhotoInstant by lazy { TakePhotoHelp.getInstant(activity!!, this, faceImgMaxSelect, cropOptions) }
    private val backImgTakePhotoInstant by lazy { TakePhotoHelp.getInstant(activity!!, this, backImgMaxSelect, cropOptions) }
    private val handImgTakePhotoInstant by lazy { TakePhotoHelp.getInstant(activity!!, this, handImgMaxSelect, cropOptions) }
    private val registerVM by lazy { ViewModelProviders.of(activity!!).get(RegisterViewModel::class.java) }
    private val repository by lazy { UserRepository() }
    val idCardNumberObs by lazy { ObservableField("") }
    val reallyNameObs by lazy { ObservableField("") }
    val registerEnableObs by lazy { ObservableField(false) }
    var faceImgId = ""
    var backImgId = ""
    var handImgId = ""

    override fun onLazyLoadOnce() {
        showContentView()
        contentView.data = this
        initView()
    }

    private val progressDialog by lazy { ProgressDialog() }
    private fun initView() {
        contentView.tvLogin.setOnClickListener {
            register()
        }
        contentView.edtIdNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isRegisterEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        contentView.edtReallyName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isRegisterEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun register() {
        repository.register(registerVM.phoneNumber, registerVM.code, registerVM.password, faceImgId, backImgId, handImgId, idCardNumberObs.get()!!, reallyNameObs.get()!!)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            progressDialog.setStart("正在努力的加载中..")
                        }
                        Status.Content -> {
                            if (it.data!!.groupIdList?.isEmpty() != false)
                                context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", -1).apply()
                            else {
                                context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", it.data!!.groupIdList!![0]).apply()
                                if (it.data!!.groupIdList!!.size > 1) {
                                    ARouter.getInstance()
                                            .build(RouterPage.MapActivity)
                                            .navigation(context)
                                }
                            }
                            progressDialog.dismiss()
                            activity!!.finish()
                        }
                        else -> {
                            progressDialog.setFiled(it.error?.message
                                    ?: "出现了一些错误..") { it.dismiss() }
                        }
                    }

                })
    }

    fun onImgClick(view: View) {
        when (view.id) {
            R.id.imgFaceAdd, R.id.imgIdFace -> {
                selectedType = 0
                faceImgMaxSelect.set(maxOf(0, 1 - faceImgCheckedPhoto.size))
                faceImgTakePhotoInstant.startPhotoChoice()
            }
            R.id.imgBackAdd, R.id.imgIdBack -> {
                selectedType = 1
                backImgMaxSelect.set(maxOf(0, 1 - backImgCheckedPhoto.size))
                backImgTakePhotoInstant.startPhotoChoice()
            }
            R.id.imgHandAdd, R.id.imgIdHand -> {
                selectedType = 2
                handImgMaxSelect.set(maxOf(0, 1 - handImgCheckedPhoto.size))
                handImgTakePhotoInstant.startPhotoChoice()
            }
        }

    }

    override fun takeSuccess(result: TResult?) {
        if (result == null) {
            toast("选择图片为空")
        } else {
            when (selectedType) {
                0 -> {
                    faceImgCheckedPhoto.clear()
                    faceImgCheckedPhoto.addAll(result.images.map { it.compressPath })
                    uploadImg(faceImgCheckedPhoto[0], selectedType)
                }
                1 -> {
                    backImgCheckedPhoto.clear()
                    backImgCheckedPhoto.addAll(result.images.map { it.compressPath })
                    uploadImg(backImgCheckedPhoto[0], selectedType)
                }
                2 -> {
                    handImgCheckedPhoto.clear()
                    handImgCheckedPhoto.addAll(result.images.map { it.compressPath })
                    uploadImg(handImgCheckedPhoto[0], selectedType)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun uploadImg(file: String, type: Int) {
        apiProvider.create(UploadApi::class.java).uploadImg(MultipartBody.Part.createFormData("file", file, convertToRequestBody(File(file))))
                .mainIoSchedulers()
                .progressSubscribe(
                        onNext = {
                            when (type) {
                                0 -> {
                                    faceImgId = it.content!!.id.toString()
                                    contentView.imgFaceAdd.visibility = View.GONE
                                    Glide.with(activity).load(file).into(contentView.imgIdFace)
                                    registerVM.scanPic(this, file, reallyNameObs, idCardNumberObs)
                                }
                                1 -> {
                                    backImgId = it.content!!.id.toString()
                                    contentView.imgBackAdd.visibility = View.GONE
                                    Glide.with(activity).load(file).into(contentView.imgIdBack)

                                }
                                2 -> {
                                    handImgId = it.content!!.id.toString()
                                    contentView.imgHandAdd.visibility = View.GONE
                                    Glide.with(activity).load(file).into(contentView.imgIdHand)
                                }
                            }
                            isRegisterEnable()
                        }
                        ,
                        onError = {
                            toast(it.message ?: "上传图片失败")
                        }
                )

    }

    private fun isRegisterEnable() {
        if (faceImgId.isNotEmpty()
                && backImgId.isNotEmpty()
                && handImgId.isNotEmpty()
                && reallyNameObs.get()!!.isNotEmpty()
                && idCardNumberObs.get()!!.isNotEmpty()) {
            registerEnableObs.set(true)
        } else
            registerEnableObs.set(false)
    }

    private fun convertToRequestBody(param: File): RequestBody =
            RequestBody.create(MediaType.parse("image/jpg"), param)

    override fun takeCancel() {
        toast("你没有选择图片")
    }

    override fun takeFail(result: TResult?, msg: String?) {
        TakePhotoHelp.showCameraPermissionRefuseDialog(activity!!)
    }
}