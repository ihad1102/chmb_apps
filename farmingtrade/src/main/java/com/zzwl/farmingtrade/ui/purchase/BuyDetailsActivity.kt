package com.zzwl.farmingtrade.ui.purchase

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeEx
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.TakePhotoHelp
import com.g.base.help.tryCatch
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.im.ImCreateHelp
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.TResult
import com.zzwl.farmingtrade.BR
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivityBuyDetailsBinding
import com.zzwl.farmingtrade.databinding.FarmingDialogTradeBinding
import com.zzwl.farmingtrade.databinding.FarmingDialogVerifyBinding
import com.zzwl.farmingtrade.extend.progressOauthDialog
import com.zzwl.farmingtrade.room.entity.remote.PurchaseDetailsEntity
import com.zzwl.farmingtrade.room.repository.PurchaseRepository
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.viewModel.PurchaseDetailsViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.math.BigDecimal


@Route(path = RouterPage.BuyDetailsActivity, extras = RouteExtras.NeedOauth)
class BuyDetailsActivity : BaseActivity<FarmingActivityBuyDetailsBinding>() {

    @Autowired
    @JvmField
    var id: Int = 0

    var unitPrice = 0.00
    var amount = 0
    var totalMoney = 0.00
    val sumObs by lazy { ObservableField("") }
    var imgUrl = ""
    private val viewModel by lazy { ViewModelProviders.of(this).get(PurchaseDetailsViewModel::class.java) }
    override var hasToolbar: Boolean = true
    private var listStatus = ListStatus.Content
    lateinit var purchaseDetailsEntity: PurchaseDetailsEntity
    private val repository by lazy { PurchaseRepository() }
    val placeholder = ObservableField<Int>(R.drawable.farming_ic_person_white_24dp)
    val isShowUnitObs by lazy { ObservableField(true) }
    val districtObs by lazy { ObservableField("") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_buy_details)
        getData()
        showContentView()
        initView()
    }

    private fun getData() {
        viewModel.getPurchaseDetails(id)
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            purchaseDetailsEntity = it.data!!
                            isShowUnitObs.set(purchaseDetailsEntity.price != "面议")
                            var tempDistrict = ""
                            it.data!!.regionList.forEachIndexed { index, region ->
                                val temp = if (index < it.data!!.regionList.size - 1) region.regionName + "," else region.regionName
                                tempDistrict += temp
                            }
                            districtObs.set(tempDistrict)
                            contentView.data = this
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.Content -> {
                                    showError(it.error?.message)
                                }
                            }

                            listStatus = ListStatus.Content
                        }

                    }

                })
    }

    private fun initView() {
        toolbar.title = "采购详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.tvTalk.setOnClickListener {
            if (TokenManage.getToken()?.userId ?: 0 != purchaseDetailsEntity.userId) {
                ImCreateHelp.createWithPurchaseViewMessage(purchaseDetailsEntity.price,
                        purchaseDetailsEntity.title,
                        purchaseDetailsEntity.quantity.toString(),
                        purchaseDetailsEntity.id.toString(),
                        purchaseDetailsEntity.userId.toString())

            } else
                toast("请选择自己之外的用户聊天")
        }
        contentView.tvIdentification.setOnClickListener {
            val inflaterDataBinding = DataBindingUtil.inflate<FarmingDialogVerifyBinding>(LayoutInflater.from(this@BuyDetailsActivity), R.layout.farming_dialog_verify, null, false)
            val builder = AlertDialog.Builder(this, R.style.MyAlertDialog)
            builder.setView(inflaterDataBinding.root)
            val dialog = builder.create()
            dialog.show()
            val window = dialog.window
            val params = window.attributes
            window.setGravity(Gravity.BOTTOM)
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.setWindowAnimations(R.style.PopupWindow)
            window.attributes = params
            inflaterDataBinding.tvConfirm.setOnClickListener { dialog.dismiss() }
        }
        contentView.tvSupplyGoods.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        val inflaterDataBinding = DataBindingUtil.inflate<FarmingDialogTradeBinding>(LayoutInflater.from(this@BuyDetailsActivity), R.layout.farming_dialog_trade, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("交易发起")
        builder.setView(inflaterDataBinding.root)
        val dialog = builder.create()
        dialog.show()

        inflaterDataBinding.edtProductPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    this@BuyDetailsActivity.onTextChanged(s.toString(), inflaterDataBinding.edtProductPrice, 2)
                    tryCatch {
                        unitPrice = s.toString().toDouble()
                    }
                    countTotalMoney()
                }
            }
        })
        inflaterDataBinding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    this@BuyDetailsActivity.onTextChanged(s.toString(), inflaterDataBinding.edtAmount, 2)
                    tryCatch {
                        amount = s.toString().toInt()
                    }
                    countTotalMoney()
                }
            }
        })

        inflaterDataBinding.setVariable(BR.sumObs, sumObs)
        inflaterDataBinding.setVariable(BR.isSupplier, false)
        inflaterDataBinding.tvUploading.setOnClickListener { startCamera() }
        inflaterDataBinding.imgAdd.setOnClickListener { startCamera() }
        inflaterDataBinding.tvExecuteTrade.setOnClickListener {
            if (imgUrl.isNotEmpty())
                createTrade()
            else
                toast("请上传交易图片")
        }

    }

    private fun startCamera() {
        TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeSuccess(result: TResult) {
                imgUrl = result.image.compressPath
            }

            override fun takeCancel() {
            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("获取图片失败..")
            }

        }, ObservableField(1))
                .startCamera()
    }

    fun countTotalMoney() {
        totalMoney = amount * unitPrice
        val bigTotalMoney = BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP)
        sumObs.set("¥:" + bigTotalMoney.toPlainString() + "元")
    }

    private fun onTextChanged(s: String?, editText: EditText, digits: Int) {
        //删除“.”后面超过2位后的数据
        if (s?.contains(".") == true) {
            if (s.length - 1 - s.indexOf(".") > digits) {
                val s1 = s.subSequence(0,
                        s.indexOf(".") + digits + 1)
                editText.setText(s1)
                editText.setSelection(s1.length) //光标移到最后
            }
        }
        //如果"."在起始位置,则起始位置自动补0
        if (s?.trim()?.substring(0) == ".") {
            val s1 = "0$s"
            editText.setText(s1)
            editText.setSelection(2)
        }

        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (s?.startsWith("0") == true
                && s.trim().length > 1) {
            if (s.substring(1, 2) != ".") {
                editText.setText(s.subSequence(0, 1))
                editText.setSelection(1)
                return
            }
        }
    }

    private fun createTrade() {
        val array = ArrayList<MultipartBody.Part>()
        array.add(MultipartBody.Part.createFormData("buyer", purchaseDetailsEntity.userId.toString()))
        array.add(MultipartBody.Part.createFormData("cropId", purchaseDetailsEntity.cropId.toString()))
        array.add(MultipartBody.Part.createFormData("cropProductId", purchaseDetailsEntity.id.toString()))
        array.add(MultipartBody.Part.createFormData("weight", amount.toString()))
        array.add(MultipartBody.Part.createFormData("totalPrice", totalMoney.toString()))
        array.add(MultipartBody.Part.createFormData("imageFiles", imgUrl, RequestBody.create(MediaType.parse("multipart/form-data"), File(imgUrl))))
        repository.createSupplier(array.toTypedArray())
                .progressOauthDialog()
                .observeExOnce(this@BuyDetailsActivity, {

                })

    }

}