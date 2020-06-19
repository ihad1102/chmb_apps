package com.zzwl.farmingtrade.ui.supplier

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import chihane.jdaddressselector.OnAddressSelectedListener
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.example.zhouwei.library.CustomPopWindow
import com.g.base.extend.dp
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.TakePhotoHelp
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.TResult
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivityPostSupplierBinding
import com.zzwl.farmingtrade.databinding.FarmingViewRecycleBinding
import com.zzwl.farmingtrade.event.RefreshTradingEvent
import com.zzwl.farmingtrade.extend.progressOauthDialog
import com.zzwl.farmingtrade.room.entity.remote.FarmingCropEntity
import com.zzwl.farmingtrade.room.repository.SupplierRepository
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.supplier.holder.CropHolder
import com.zzwl.farmingtrade.ui.supplier.holder.PicHolder
import com.zzwl.farmingtrade.viewModel.PostSupplierViewModel
import com.zzwl.farmingtrade.widget.AddressSelectDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 *  type 0,1 :  发布供应,修改供应
 *
 */
@Route(path = RouterPage.PostSupplierActivity, extras = RouteExtras.NeedOauth)
class PostSupplierActivity : BaseActivity<FarmingActivityPostSupplierBinding>(), PopupWindow.OnDismissListener {
    @Autowired
    @JvmField
    var type = 0
    @Autowired
    @JvmField
    var id = ""
    override var hasToolbar: Boolean = true
    val cropNameObs by lazy { ObservableField("请选择你要出售的作物种类") }
    val supplierAmountObs by lazy { ObservableField("") }
    val wholesaleNumObs by lazy { ObservableField("") }
    val specificationObs by lazy { ObservableField("") }
    val packageObs by lazy { ObservableField("") }
    val districtObs by lazy { ObservableField("请选择你的货源地区") }
    val priceObs by lazy { ObservableField("") }
    var tempPrice = ""
    val otherInfoObs by lazy { ObservableField("") }
    var cropId = ""
    var plantingId = ""
    var districtId = ""
    var annual = ""
    var isExpressage = "0"  //是否支持快递,
    private var isRetrospect = "0" //是否关联农事追溯
    val enabledObs by lazy { ObservableField(false) }
    private lateinit var popupWindow: PopupWindow
    private val pictureList by lazy { ArrayList<String>() }
    private lateinit var addressSelectDialog: AddressSelectDialog
    val repository by lazy { SupplierRepository() }
    val viewModel by lazy { ViewModelProviders.of(this).get(PostSupplierViewModel::class.java) }
    var takePhoto: TakePhotoHelp? = null
    var isPutaway = "0"
    private val pictureAdapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private var listStatus = ListStatus.Content
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_activity_post_supplier)
        ARouter.getInstance().inject(this)
        contentView.data = this
        initView()
        getData()
    }


    private fun getData() {
        if (type != 0) {
            viewModel.getSupplier(id)
                    .resultNotNull()
                    .observeExOnce(this) {
                        when (it.status) {
                            Status.Loading -> {
                                if (listStatus == ListStatus.Content)
                                    showLoading()
                            }
                            Status.Content -> {
                                contentView.data = this
                                viewModel.updateSupplierEntitiy = it.data!!
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
        } else {
            showContentView()
        }
    }

    private fun bindView() {
        isPutaway = if (viewModel.updateSupplierEntitiy?.isPutaway == true) "1" else "0"
        cropNameObs.set(viewModel.updateSupplierEntitiy?.title)
        cropId = viewModel.updateSupplierEntitiy?.cropId?.toString() ?: ""
        plantingId = viewModel.updateSupplierEntitiy?.plantingId?.toString() ?: ""
        annual = viewModel.updateSupplierEntitiy?.plantingVo?.annual?.toString() ?: ""
        districtId = viewModel.updateSupplierEntitiy?.regionId?.toString() ?: ""
        isRetrospect = if (viewModel.updateSupplierEntitiy?.isRetrospect == true) {
            contentView.cbFarming.isChecked = true
            "1"
        } else "0"
        supplierAmountObs.set(viewModel.updateSupplierEntitiy?.quantity.toString())
        wholesaleNumObs.set(viewModel.updateSupplierEntitiy?.minQuantity.toString())
        specificationObs.set(viewModel.updateSupplierEntitiy?.specification)
        packageObs.set(viewModel.updateSupplierEntitiy?.packType)
        districtObs.set(viewModel.updateSupplierEntitiy?.address)
        otherInfoObs.set(viewModel.updateSupplierEntitiy?.otherInfo)
        if (viewModel.updateSupplierEntitiy?.isShip == true) {
            contentView.radioButton.isChecked = true
        } else {
            contentView.radioButton2.isChecked = true
        }
        if (viewModel.updateSupplierEntitiy?.price != 0.0)
            priceObs.set(viewModel.updateSupplierEntitiy?.price.toString())
        else {
            priceObs.set("面议")
            contentView.cbPrice.isChecked = true
        }
//        if (viewModel.updateSupplierEntitiy?.imageList?.isNotEmpty() == true)
//            if (contentView.recyclerView.visibility == View.GONE)
//                contentView.recyclerView.visibility = View.VISIBLE
//        viewModel.updateSupplierEntitiy?.imageList?.forEach {
//            if (pictureList.size >= 3) return
//            pictureList.add(it.url)
//        }
//        bindRecyclerView()
    }

    private fun initView() {
        toolbar.title = if (type == 0) "发布供应" else "修改供应"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.tvCrop.setOnClickListener {
            requestCropData()
        }
        contentView.tvConfirm.setOnClickListener {
            postFarmProduct()

        }
        contentView.tvLocation.setOnClickListener {
            openAddressSelect(it)
        }
        contentView.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton -> isExpressage = "1"
                R.id.radioButton2 -> isExpressage = "0"
            }
        }
        contentView.cbPrice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (priceObs.get()!! != "面议")
                    tempPrice = priceObs.get()!!
                priceObs.set("面议")
            } else {
                priceObs.set(tempPrice)
            }
        }
        contentView.imgAddPicture.setOnClickListener {
            openPopupWindow()
        }
        contentView.tvAddPicture.setOnClickListener {
            openPopupWindow()
        }
        contentView.cbFarming.setOnCheckedChangeListener { _, isChecked ->
            isRetrospect = if (isChecked) "1" else "0"

        }
        contentView.imgQuestionMark.setOnClickListener {
            val customPopWindow = CustomPopWindow.PopupWindowBuilder(this)
                    .setView(R.layout.farming_dialog_retrospect)
                    .setFocusable(false)
                    .setOutsideTouchable(true)
                    .create()
            customPopWindow.showAsDropDown(contentView.imgQuestionMark, -(40.dp()), -(contentView.imgQuestionMark.height + 60.dp()))
        }
    }


    private fun openAddressSelect(it: View) {
        val imm = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
        addressSelectDialog = AddressSelectDialog.show(this, OnAddressSelectedListener { province, city, county, _ ->
            districtObs.set(province.name + city.name + county.name)
            districtId = county.id.toString()
            addressSelectDialog.dismiss()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }, false)
    }

    private fun requestCropData() {
        repository.farmingCropList()
                .resultNotNull()
                .progressOauthDialog()
                .observeExOnce(this) {
                    when (it.status) {
                        Status.Content -> {
                            openDialog(it.data!!)
                        }
                    }
                }
    }


    private fun openDialog(cropList: List<FarmingCropEntity>) {
        val inflaterDataBinding = DataBindingUtil.inflate<FarmingViewRecycleBinding>(LayoutInflater.from(this@PostSupplierActivity), R.layout.farming_view_recycle, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("请选择你要出售的作物")
        builder.setView(inflaterDataBinding.root)
        val dialog = builder.create()
        dialog.show()


        val adapter = setupRecyclerView(inflaterDataBinding.recyclerView)
        cropList.forEach {
            adapter.renderItems.add(
                    CropHolder(it).apply {
                        setOnClickListener {
                            cropNameObs.set(framingCropEntity.cropName)
                            cropId = framingCropEntity.cropId.toString()
                            plantingId = framingCropEntity.plantingId.toString()
                            annual = framingCropEntity.annual.toString()
                            dialog.dismiss()
                        }
                    })
            adapter.notifyDataSetChanged()
        }

    }

    private fun postFarmProduct() {
        val array = ArrayList<MultipartBody.Part>()
        if (type != 0) {
            array.add(MultipartBody.Part.createFormData("id", id))
            array.add(MultipartBody.Part.createFormData("isPutaway", isPutaway))
        }
        array.add(MultipartBody.Part.createFormData("title", cropNameObs.get()!!))
        array.add(MultipartBody.Part.createFormData("cropId", cropId))
        array.add(MultipartBody.Part.createFormData("plantingId", plantingId))
        array.add(MultipartBody.Part.createFormData("quantity", supplierAmountObs.get()!!))
        array.add(MultipartBody.Part.createFormData("minQuantity", wholesaleNumObs.get()!!))
        if (priceObs.get()!! != "面议")
            array.add(MultipartBody.Part.createFormData("price", priceObs.get()!!))
        array.add(MultipartBody.Part.createFormData("packType", packageObs.get()!!))
        array.add(MultipartBody.Part.createFormData("specification", specificationObs.get()!!))
        array.add(MultipartBody.Part.createFormData("otherInfo", otherInfoObs.get()!!))
        array.add(MultipartBody.Part.createFormData("annual", annual))
        array.add(MultipartBody.Part.createFormData("regionId", districtId))
        array.add(MultipartBody.Part.createFormData("isRetrospect", isRetrospect))
        array.add(MultipartBody.Part.createFormData("isShip", isExpressage))

        pictureList.forEach {
            if (it.isNotEmpty())
                array.add(MultipartBody.Part.createFormData("imageFiles", it, RequestBody.create(MediaType.parse("multipart/form-data"), File(it))))
        }
        if (type == 0)
            repository.postFramingProduct(array.toTypedArray())
                    .progressOauthDialog({
                        it.dismiss()
                        toast("发布成功")
                        finish()
                    })
                    .observeExOnce(this, {})
        else {
            repository.updateProduct(array.toTypedArray())
                    .progressOauthDialog({
                        it.dismiss()
                        toast("发布成功")
                        EventBus.getDefault().post(RefreshTradingEvent(true))
                        finish()
                    })
                    .observeExOnce(this, {})
        }
    }

    private fun bindRecyclerView() {
        pictureAdapter.renderItems.clear()
        val gridLayoutHelper = GridLayoutHelper(4, pictureList.size)
        gridLayoutHelper.hGap = 8.dp()
        gridLayoutHelper.setAutoExpand(false)
        pictureAdapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        pictureList.forEachIndexed { index, s ->
            pictureAdapter.renderItems.add(PicHolder(s, true, isPostPic = true).apply {
                setOnClickListener { view ->
                    when (view.id) {
                        R.id.imgCancel -> {
                            pictureList.removeAt(index)
                            viewModel.maxSelect.set(3 - pictureList.size)
                            if (pictureList.size > 0) {
                                bindRecyclerView()
                            } else
                                contentView.recyclerView.visibility = View.GONE
                            onEditTextChange()
                        }
                    }
                }

            })
        }
        pictureAdapter.notifyDataSetChanged()
    }

    fun onEditTextChange() {
        if (priceObs.get()!!.isEmpty()) contentView.cbPrice.isChecked = false
        if (cropNameObs.get()!! != "请选择你要出售的作物种类" &&
                cropNameObs.get()!!.isNotEmpty() &&
                supplierAmountObs.get()!!.isNotEmpty() &&
                supplierAmountObs.get()!! != "0" &&
                wholesaleNumObs.get()!!.isNotEmpty() &&
                wholesaleNumObs.get()!! != "0" &&
                specificationObs.get()!!.isNotEmpty() &&
                packageObs.get()!!.isNotEmpty() &&
                districtObs.get()!! != "请选择你的货源地区" &&
                priceObs.get()!!.isNotEmpty() &&
                priceObs.get()!! != "0" &&
                (pictureList.isNotEmpty() || type == 1)
        )
            enabledObs.set(true)
        else
            enabledObs.set(false)
    }

    override fun onDismiss() {
        backgroundAlpha(1f)
    }


    private fun backgroundAlpha(bgAlpha: Float) {
        val layoutParams = this.window.attributes
        layoutParams.alpha = bgAlpha
        this.window.attributes = layoutParams
    }

    private fun startGallery() {
        takePhoto = TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeCancel() {

            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("出现异常,请重新选取")

            }

            override fun takeSuccess(result: TResult) {
                if (contentView.recyclerView.visibility == View.GONE)
                    contentView.recyclerView.visibility = View.VISIBLE
                result.images.forEach {
                    pictureList.add(it.compressPath)
                }
                bindRecyclerView()
                onEditTextChange()
            }
        }, viewModel.maxSelect)
        takePhoto?.startGallery()
    }

    private fun startCamera() {
        takePhoto = TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeSuccess(result: TResult) {
                if (contentView.recyclerView.visibility == View.GONE)
                    contentView.recyclerView.visibility = View.VISIBLE
                if (pictureList.size >= 3) return
                pictureList.add(result.images[0].compressPath)
                bindRecyclerView()
                onEditTextChange()
            }

            override fun takeCancel() {
            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("获取图片失败..")
            }

        }, ObservableField(1))

        takePhoto?.startCamera()
    }

    fun openPopupWindow() {
        //设置PopupWindow
        val view1: View = LayoutInflater.from(this).inflate(R.layout.farming_item_popup_window2, null)
        popupWindow = PopupWindow(view1, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.animationStyle = R.style.PopupWindow
        popupWindow.showAtLocation(view1, Gravity.BOTTOM, 0, 0)
        popupWindow.setOnDismissListener(this)
        backgroundAlpha(0.7f)

    }

    fun onPopupViewClick(view: View) {
        when (view.id) {
            R.id.tvCamera -> {
                startCamera()
                popupWindow.dismiss()
            }
            R.id.tvGallery -> {
                startGallery()
                popupWindow.dismiss()
            }
            R.id.tvCancel -> {
                popupWindow.dismiss()
            }
        }
    }


}