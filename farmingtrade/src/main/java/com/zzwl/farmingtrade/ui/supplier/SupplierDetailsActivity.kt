package com.zzwl.farmingtrade.ui.supplier

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
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.im.ImCreateHelp
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.TResult
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.farmingtrade.BR
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivitySupplierDetailsBinding
import com.zzwl.farmingtrade.databinding.FarmingDialogTradeBinding
import com.zzwl.farmingtrade.databinding.FarmingDialogVerifyBinding
import com.zzwl.farmingtrade.extend.progressOauthDialog
import com.zzwl.farmingtrade.room.entity.remote.Image
import com.zzwl.farmingtrade.room.entity.remote.SubsidiesRulesEntity
import com.zzwl.farmingtrade.room.entity.remote.SupplierEntity
import com.zzwl.farmingtrade.room.repository.SupplierRepository
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.viewModel.SupplierViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.math.BigDecimal

@Route(path = RouterPage.SupplierDetailsActivity)
class SupplierDetailsActivity : BaseActivity<FarmingActivitySupplierDetailsBinding>() {


    @Autowired
    @JvmField
    var id: Int = 0
    override var hasToolbar: Boolean = true
    private val viewModel by lazy { ViewModelProviders.of(this).get(SupplierViewModel::class.java) }
    private var listStatus = ListStatus.Content
    lateinit var supplierEntity: SupplierEntity
    val priceObs by lazy { ObservableField("") }
    val repository by lazy { SupplierRepository() }
    val sumObs by lazy { ObservableField("") }
    val subsidyObs by lazy { ObservableField("") }
    var imgUrl = ""
    var unitPrice = 0.00
    var amount = 0
    var totalMoney = 0.00
    var ruleList = ArrayList<SubsidiesRulesEntity>()//补贴规则

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_supplier_details)
        initToolbar()
        onReload()
    }

    private fun initToolbar() {
        toolbar.title = "供应详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        contentView.tvTalk.setOnClickListener {
            val tempImgUrl = if (supplierEntity.imageList.isNotEmpty()) supplierEntity.imageList[0].url else ""
            if (TokenManage.getToken()?.userId ?: 0 != supplierEntity.userId) {
                ImCreateHelp.createWithFarmingViewMessage(supplierEntity.price, supplierEntity.title,
                        tempImgUrl, supplierEntity.id.toString(), supplierEntity.userId.toString())
            } else
                toast("请选择自己之外的用户聊天")

        }
        contentView.tvReceivingGood.setOnClickListener {
            if (TokenManage.getToken()?.userId ?: 0 != supplierEntity.userId.toString()) {
                getRule()
            } else
                toast("请选择自己之外的用户交易")

        }
    }

    private fun getRule() {
        repository.getSubsidiesRules(supplierEntity.cropId)
                .progressOauthDialog()
                .observeExOnce(this) {
                    when (it.status) {
                        Status.Content -> {
                            ruleList = it.data as ArrayList<SubsidiesRulesEntity>
                            openDialog()
                        }
                        Status.Error -> {
                            openDialog()
                        }
                    }

                }
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

    override fun onReload() {
        getData()
    }

    private fun getData() {
        viewModel.getSupplierDetails(id.toString())
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            supplierEntity = it.data!!
                            val tempPrice = if (it.data!!.price != "面议") "${it.data!!.price}元/公斤" else "面议"
                            priceObs.set(tempPrice)
                            contentView.data = this@SupplierDetailsActivity
                            initView(it.data!!.imageList, it.data!!.plantingId)
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

                }
    }

    private fun initView(imageList: List<Image>, id: Int) {
        contentView.tvRetrospect.setOnClickListener {
            ARouter.getInstance().build(RouterPage.FarmingRetrospectActivity).withInt("id", id).navigation(this)
        }
        val fragmentPages = FragmentPagerItems(this)
        imageList.forEach {
            fragmentPages.add(FragmentPagerItem.of("page", ViewPagerFragment::class.java
                    , Bundle().apply {
                putString("bannerUrl", it.url)
                putInt("ScaleType", 6)
            }))
        }
        val adapter = FragmentPagerItemAdapter(supportFragmentManager, fragmentPages)
        contentView.viewPager.adapter = adapter
        contentView.tabIndicator.setViewPager(contentView.viewPager)
        contentView.tvVerify.setOnClickListener {
            val inflaterDataBinding = DataBindingUtil.inflate<FarmingDialogVerifyBinding>(LayoutInflater.from(this@SupplierDetailsActivity), R.layout.farming_dialog_verify, null, false)
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

    private fun openDialog() {
        val inflaterDataBinding = DataBindingUtil.inflate<FarmingDialogTradeBinding>(LayoutInflater.from(this@SupplierDetailsActivity), R.layout.farming_dialog_trade, null, false)
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
                    this@SupplierDetailsActivity.onTextChanged(s.toString(), inflaterDataBinding.edtProductPrice, 2)
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
                    this@SupplierDetailsActivity.onTextChanged(s.toString(), inflaterDataBinding.edtAmount, 2)
                    tryCatch {
                        amount = s.toString().toInt()
                        var subsidy = 0.00
                        for (it in ruleList) {
                            if (amount >= it.minQuantity) {
                                subsidy = it.money
                                break
                            }
                        }
                        val tempMoney: Int = amount / 1000
                        val bigDecimal = BigDecimal(tempMoney * subsidy)
                        subsidyObs.set(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
                    }
                    countTotalMoney()
                }
            }
        })

        inflaterDataBinding.setVariable(BR.subsidyObs, subsidyObs)
        inflaterDataBinding.setVariable(BR.sumObs, sumObs)
        inflaterDataBinding.setVariable(BR.isSupplier, true)
        inflaterDataBinding.tvUploading.setOnClickListener { startCamera() }
        inflaterDataBinding.imgAdd.setOnClickListener { startCamera() }
        inflaterDataBinding.tvExecuteTrade.setOnClickListener {
            if (imgUrl.isNotEmpty())
                createTrade(dialog)
            else
                toast("请上传交易图片")
        }

    }

    fun countTotalMoney() {
        totalMoney = amount * unitPrice
        val bigTotalMoney = BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP)
        sumObs.set("¥:" + bigTotalMoney.toPlainString() + "元")
    }

    private fun createTrade(dialog: AlertDialog) {
        val array = ArrayList<MultipartBody.Part>()
        array.add(MultipartBody.Part.createFormData("seller", supplierEntity.userId.toString()))
        array.add(MultipartBody.Part.createFormData("cropId", supplierEntity.cropId.toString()))
        array.add(MultipartBody.Part.createFormData("cropProductId", supplierEntity.id.toString()))
        array.add(MultipartBody.Part.createFormData("weight", amount.toString()))
        array.add(MultipartBody.Part.createFormData("totalPrice", totalMoney.toString()))
        array.add(MultipartBody.Part.createFormData("imageFiles", imgUrl, RequestBody.create(MediaType.parse("multipart/form-data"), File(imgUrl))))
        repository.createPurchase(array.toTypedArray())
                .progressOauthDialog({
                    it.dismiss()
                    dialog.dismiss()
                    finish()
                })
                .observeExOnce(this@SupplierDetailsActivity, {})

    }


}
