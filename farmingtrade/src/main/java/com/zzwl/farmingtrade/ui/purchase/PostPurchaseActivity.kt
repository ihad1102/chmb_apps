package com.zzwl.farmingtrade.ui.purchase

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import chihane.jdaddressselector.OnAddressSelectedListener
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeEx
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivityPostPurchaseBinding
import com.zzwl.farmingtrade.event.CropIdEvent
import com.zzwl.farmingtrade.event.RefreshTradingEvent
import com.zzwl.farmingtrade.extend.progressOauthDialog
import com.zzwl.farmingtrade.room.repository.PurchaseRepository
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.viewModel.PostPurchaseViewModel
import com.zzwl.farmingtrade.widget.AddressSelectDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  type 0,1 : 发布采购,修改采购
 */
@Route(path = RouterPage.PostPurchaseActivity, extras = RouteExtras.NeedOauth)
class PostPurchaseActivity : BaseActivity<FarmingActivityPostPurchaseBinding>() {
    @Autowired
    @JvmField
    var type = 0

    @Autowired
    @JvmField
    var id = ""

    override var hasToolbar: Boolean = true
    val enabledObs by lazy { ObservableField(false) }
    val amountObs by lazy { ObservableField("") }
    val kindObs by lazy { ObservableField("请选择你要采购的货品种类") }
    val kindColorObs by lazy { ObservableField(false) }
    val specificationObs by lazy { ObservableField("") }
    val packageObs by lazy { ObservableField("") }
    val district1Obs by lazy { ObservableField("") }
    val district2Obs by lazy { ObservableField("") }
    val district3Obs by lazy { ObservableField("") }
    var districtId1 = ""
    var districtId2 = ""
    var districtId3 = ""
    private var tempDistrict1 = ""
    private var tempDistrict2 = ""
    private var tempDistrict3 = ""
    private var listStatus = ListStatus.Content

    val priceObs by lazy { ObservableField("") }
    private var tempPrice = ""
    private val repository by lazy { PurchaseRepository() }
    val isShow2Obs by lazy { ObservableField(false) }
    val isShow3Obs by lazy { ObservableField(false) }
    var cropId = ""
    val otherInfoObs by lazy { ObservableField("") }
    val viewModel by lazy { ViewModelProviders.of(this).get(PostPurchaseViewModel::class.java) }
    var userId = ""
    var doing = false
    private lateinit var addressSelectDialog: AddressSelectDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_post_purchase)
        contentView.data = this
        initView()
        onReload()
    }


    override fun onReload() {
        if (type != 0) {
            viewModel.getPurchase(id)
                    .resultNotNull()
                    .observeExOnce(this) {
                        when (it.status) {
                            Status.Loading -> {
                                if (listStatus == ListStatus.Content)
                                    showLoading()
                            }
                            Status.Content -> {
                                contentView.data = this
                                viewModel.data = it.data!!
                                bindView()
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
        } else
            showContentView()

    }

    private fun bindView() {
        userId = viewModel.data?.userId ?: ""
        doing = viewModel.data?.doing ?: false
        cropId = viewModel.data?.cropId ?: ""
        kindObs.set(viewModel.data?.title)
        amountObs.set(viewModel.data?.quantity)
        specificationObs.set(viewModel.data?.specification)
        packageObs.set(viewModel.data?.packType)
        priceObs.set(viewModel.data?.price)
        if (viewModel.data?.price == "面议") contentView.checkBoxPrice.isChecked = true
        otherInfoObs.set(viewModel.data?.otherInfo)
        if (viewModel.data?.regions == "1") {
            contentView.checkBoxDistrict.isChecked = true
            district1Obs.set("全国")
            districtId1 = "1"
        } else {
            if (viewModel.data?.regionList?.isNotEmpty() == true)
                district1Obs.set(viewModel.data?.regionList?.get(0)?.regionName ?: "")
            districtId1 = viewModel.data?.regionList?.get(0)?.regionId ?: ""
            if ((viewModel.data?.regionList?.size ?: 0) > 1) {
                isShow2Obs.set(true)
                district2Obs.set(viewModel.data?.regionList?.get(1)?.regionName ?: "")
                districtId2 = viewModel.data?.regionList?.get(1)?.regionId ?: ""
            }
            if ((viewModel.data?.regionList?.size ?: 0) > 2) {
                isShow3Obs.set(true)
                district3Obs.set(viewModel.data?.regionList?.get(2)?.regionName ?: "")
                districtId3 = viewModel.data?.regionList?.get(2)?.regionId ?: ""
            }
        }
    }


    private fun initView() {
        toolbar.title = if (type == 0) "发布采购" else "编辑采购"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.tvGoodsKind.setOnClickListener {
            ARouter.getInstance().build(RouterPage.CropsActivity)
                    .withBoolean("isFollowed", false)
                    .navigation(this)
        }

        contentView.edtDistrict1.setOnClickListener {
            isShow2Obs.set(true)
            addressDialog(it, district1Obs, isShow2Obs, 1)
        }
        contentView.edtDistrict2.setOnClickListener {
            isShow3Obs.set(true)
            addressDialog(it, district2Obs, isShow3Obs, 2)
        }
        contentView.edtDistrict3.setOnClickListener {
            addressDialog(it, district3Obs, null, 3)
        }

        contentView.tvConfirm.setOnClickListener {
            if (type == 0)
                postPurchase()
            else
                updatePurchase()
        }
        contentView.viewCancel2.setOnClickListener {
            districtId2 = ""
            district2Obs.set("")
        }
        contentView.viewCancel3.setOnClickListener {
            districtId3 = ""
            district3Obs.set("")
        }
        //district 无法编辑 所以无法获取焦点,所以监听其他edittext的焦点变化
        contentView.edtAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                districtLoseFocus()
        }
        contentView.edtSpecification.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                districtLoseFocus()
        }
        contentView.edtPackage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                districtLoseFocus()
        }
        contentView.edtPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                districtLoseFocus()
        }
        contentView.tvOtherInfo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                districtLoseFocus()
        }
        contentView.checkBoxDistrict.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tempDistrict1 = district1Obs.get()!!
                tempDistrict2 = district2Obs.get()!!
                tempDistrict3 = district3Obs.get()!!
                district1Obs.set("全国")
                district2Obs.set("")
                district3Obs.set("")
            } else {
                district1Obs.set(tempDistrict1)
                district2Obs.set(tempDistrict2)
                district3Obs.set(tempDistrict3)
            }
        }
        contentView.checkBoxPrice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (priceObs.get()!! != "面议")
                    tempPrice = priceObs.get()!!
                priceObs.set("面议")
            } else {
                priceObs.set(tempPrice)
            }
        }
    }

    private fun updatePurchase() {
        var tempDistrictId = ""
        val tempPrice = if (priceObs.get()!! == "面议") null else priceObs.get()!!
        if (district1Obs.get()!!.isNotEmpty())
            tempDistrictId = "$districtId1,"
        if (district2Obs.get()!!.isNotEmpty())
            tempDistrictId = "$tempDistrictId$districtId2,"
        if (district3Obs.get()!!.isNotEmpty())
            tempDistrictId = "$tempDistrictId$districtId3,"
        if (tempDistrictId.isNotEmpty())
            tempDistrictId = tempDistrictId.substring(0, tempDistrictId.length - 1)
        if (district1Obs.get() == "全国") tempDistrictId = "1"
        repository.upDatePurchase(
                id,
                cropId,
                kindObs.get()!!,
                tempDistrictId,
                amountObs.get()!!,
                tempPrice,
                packageObs.get()!!,
                specificationObs.get()!!,
                doing,
                userId,
                otherInfoObs.get()!!
        ).progressOauthDialog({
            it.dismiss()
            toast("发布成功")
            EventBus.getDefault().post(RefreshTradingEvent(true))
            finish()
        }).observeEx(this) { }
    }

    private fun postPurchase() {
        var tempDistrictId = ""
        val tempPrice = if (priceObs.get()!! == "面议") null else priceObs.get()!!
        if (district1Obs.get()!!.isNotEmpty())
            tempDistrictId = "$districtId1,"
        if (district2Obs.get()!!.isNotEmpty())
            tempDistrictId = "$tempDistrictId$districtId2,"
        if (district3Obs.get()!!.isNotEmpty())
            tempDistrictId = "$tempDistrictId$districtId3,"
        if (tempDistrictId.isNotEmpty())
            tempDistrictId = tempDistrictId.substring(0, tempDistrictId.length - 1)
        if (district1Obs.get() == "全国") tempDistrictId = "1"
        repository.postPurchase(
                cropId,
                kindObs.get()!!,
                tempDistrictId,
                amountObs.get()!!,
                tempPrice,
                packageObs.get()!!,
                specificationObs.get()!!,
                otherInfoObs.get()!!
        ).progressOauthDialog({
            it.dismiss()
            toast("发布成功")
            finish()
        })
                .observeEx(this) { }

    }

    private fun addressDialog(it: View, districtObs: ObservableField<String>, showObs: ObservableField<Boolean>?, type: Int) {
        val imm = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
        addressSelectDialog = AddressSelectDialog.show(this, OnAddressSelectedListener { province, city, county, _ ->
            showObs?.set(true)
            districtObs.set(province.name + city.name + county.name)
            when (type) {
                1 -> districtId1 = county.id.toString()
                2 -> districtId2 = county.id.toString()
                3 -> districtId3 = county.id.toString()
            }
            addressSelectDialog.dismiss()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }, false)
    }

    fun onEditTextChange() {
        if (cropId.isNotEmpty() &&
                amountObs.get()!!.isNotEmpty() &&
                amountObs.get()!! != "0" &&
                specificationObs.get()!!.isNotEmpty() &&
                packageObs.get()!!.isNotEmpty() &&
                (district1Obs.get()!!.isNotEmpty() || district2Obs.get()!!.isNotEmpty() || district3Obs.get()!!.isNotEmpty()) &&
                priceObs.get()!!.isNotEmpty() &&
                priceObs.get()!! != "0" &&
                kindObs.get()!! != "请选择你要采购的货品种类") {
            enabledObs.set(true)
        } else
            enabledObs.set(false)
    }

    private fun districtLoseFocus() {
        if (district2Obs.get()!!.isEmpty()) {
            isShow2Obs.set(false)
        }
        if (district3Obs.get()!!.isEmpty()) {
            isShow3Obs.set(false)
        }
        if (district1Obs.get()!!.isEmpty()) {
            if (district2Obs.get()!!.isNotEmpty()) {
                district1Obs.set(district2Obs.get())
                district2Obs.set("")
                isShow2Obs.set(false)
            } else if (district3Obs.get()!!.isNotEmpty()) {
                district1Obs.set(district3Obs.get())
                district3Obs.set("")
                isShow3Obs.set(false)
            }
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(cropIdEvent: CropIdEvent) {
        kindObs.set(cropIdEvent.cropName)
        cropId = cropIdEvent.cropId.toString()
    }
}