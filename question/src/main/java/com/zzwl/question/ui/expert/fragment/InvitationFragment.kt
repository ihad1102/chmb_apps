package com.zzwl.question.ui.expert.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.PopupWindow
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemPopupFiltrateBinding
import com.zzwl.question.databinding.QuestionViewRecyclerviewBinding
import com.zzwl.question.event.RefreshExpertEvent
import com.zzwl.question.room.entity.common.ExpertCEntity
import com.zzwl.question.room.repository.LocationRepository
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.expert.InvitationActivity
import com.zzwl.question.ui.expert.holder.ExpertFiltrateHolder
import com.zzwl.question.ui.expert.holder.ExpertHolder
import com.zzwl.question.ui.expert.holder.SearchHolder
import com.zzwl.question.ui.expert.holder.TitleFiltrateHolder
import com.zzwl.question.viewModel.ExpertViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by qhn on 2018/1/3.
 * 我关注的,全部专家fragment
 * type0,1
 */

class InvitationFragment : BaseFragment<QuestionViewRecyclerviewBinding>(), PopupWindow.OnDismissListener {

    override fun setContentView(): Int = R.layout.question_view_recyclerview

    @Autowired
    @JvmField
    var isFollow: Boolean = false

    private val expertTagMap by lazy { HashMap<String, String>() }
    private val cropTypeMap by lazy { HashMap<String, String>() }
    private var listStatus = ListStatus.Content
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private val expertViewModel by lazy { ViewModelProviders.of(this).get(ExpertViewModel::class.java) }
    private val locationRepository by lazy { LocationRepository() }
    private val data by lazy { ArrayList<ExpertCEntity>() }
    private val locationArray by lazy { IntArray(2) }
    private val locationObs by lazy { ObservableField("北京") }
    private val viewBackgroundObs by lazy { ObservableField<Int>(R.color.colorPageBgDark) }
    private val viewTextColorObs by lazy { ObservableField<Int>(ContextCompat.getColor(context!!, R.color.colorTextDarkWeek)) }
    private var isSelectedCity = false  //地址按键是否被点击选择
    val lookExpertObs by lazy { ObservableField("查看全部专家") }
    //定位相关
    private var listener: LocationListener? = null
    private val locationManager by lazy { (context!!.getSystemService(Context.LOCATION_SERVICE)) as LocationManager }

    private var previousTypeObs: ObservableField<Boolean>? = null //上一个被点击的button
    private var previousCropObs: ObservableField<Boolean>? = null

    //第二次打开显示上次被选中button的obs,, 提为全局变量,给清除标签使用
    private var expertTempObs: ObservableField<Boolean>? = null
    private var cropTempObs: ObservableField<Boolean>? = null

    private val expertTypeObs by lazy { ObservableField<String?>() }    //要传给服务器的参数
    private val cropIdObs by lazy { ObservableField<String?>() }

    //    private val cityIdObsTemp by lazy { ObservableField<String?>() }  //提为全局变量,给清除标签使用
    private val cityIdObs by lazy { ObservableField<String?>("52") }    //要传给服务器的参数 默认北京
    //是否选中地区按键
    private val isSelectedDistrictObs by lazy { ObservableField(false) }

    @SuppressLint("CheckResult")
    override fun onLazyLoadOnce() {
        ARouter.getInstance().inject(this)
        initView()
//        if (!isFollow) {
//            getLocation()
//        }
        onReload()
        obsData()
    }


    private fun initView() {
        contentView.recyclerView.getLocationOnScreen(locationArray)
    }

    override fun onReload() {
        expertViewModel.operateExpertList(0, isFollow, null, null, null, null)
    }

    private fun obsData() {
        expertViewModel.obsExpertList()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                data.addAll(it.data!!)
                            } else {
                                data.clear()
                                data.addAll(it.data!!)
                                adapter.setOnLoadingListener {
                                    if (listStatus == ListStatus.Content) {
                                        val tempCityId: String? = if (isSelectedCity) cityIdObs.get() else null
                                        expertViewModel.operateExpertList(data.size, null, expertTypeObs.get(), tempCityId, null, cropIdObs.get())
                                        listStatus = ListStatus.LoadMore
                                    } else {
                                        adapter.setLoadingFailed()
                                        toast("正在执行其他操作请稍后再试")
                                    }

                                }

                            }
                            if (it.data!!.size < expertViewModel.limit) {
                                adapter.setLoadingNoMore()
                            } else {
                                adapter.setLoadingSucceed()
                            }
                            applyData()
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            if (listStatus == ListStatus.LoadMore) {
                                if (it.error?.code == ErrorCode.EMPTY) {
                                    adapter.setLoadingNoMore()
                                } else {
                                    adapter.setLoadingFailed()
                                }
                            } else {
                                showError(it.error?.message)
                                data.clear()
                            }
                            applyData()
                            listStatus = ListStatus.Content
                        }
                    }
                }
    }

    private fun applyData() {
        val helperSize = if (!isFollow) data.size else data.size
        val linearLayoutHelper = LinearLayoutHelper(8.dp(), helperSize)
        linearLayoutHelper.setMargin(0, 8.dp(), 0, 12.dp())


        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        addSearchHolder()
        adapter.diffItems.addAll(data.map {
            val invitationActivity = activity as InvitationActivity
            val isCheck = invitationActivity.checkObserver.getOrPut(it.id, { ObservableField(false) })
            ExpertHolder(it, isCheck = isCheck).apply {
                setOnClickListener { view ->
                    when (view.id) {
                        R.id.checkBox -> {
                            val checkData = invitationActivity.checkData!!
                            if (isCheck.get() == true) {
                                (activity as InvitationActivity).changeMenu(1)
                                if (checkData.size >= 3) {
                                    isCheck.set(false)
                                    toast("最多邀请3位专家")
                                } else {
                                    checkData[it.id] = it.userName
                                }
                            } else {
                                checkData.remove(it.id)
                                if (checkData.isEmpty()) {
                                    (activity as InvitationActivity).changeMenu(0)
                                }
                            }
                        }
                        R.id.clickView -> {
                            ARouter.getInstance().build(RouterPage.ExpertInformationActivity).withString("id", it.id).navigation()
                        }
                    }
                }
            }
        })

        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    when {
                        baseItem is SearchHolder && baseItem1 is SearchHolder -> {
                            true
                        }
                        baseItem is ExpertHolder && baseItem1 is ExpertHolder -> {
                            (baseItem).expert?.id == baseItem1.expert?.id
                        }
                        else -> false
                    }

                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    when {
                        baseItem is SearchHolder && baseItem1 is SearchHolder -> {
                            true
                        }
                        baseItem is ExpertHolder && baseItem1 is ExpertHolder -> {
                            baseItem.expert?.id == baseItem1.expert?.id
                        }
                        else -> false
                    }
                }
        )
    }

    private fun addSearchHolder() {
        if (!isFollow) {
//            adapter.diffItems.add(SearchHolder(expertTypeObs, cropIdObs, cityIdObs, expertViewModel).apply {
//                setOnClickListener {
            //                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(it.windowToken, 0)
//                    expertViewModel.getExpertAllType(this@InvitationFragment)
//                            .take(1)
//                            .progressSubscribe(
//                                    onNext = {
//                                        it.expertType.data!!.filter { it?.title != null }.forEach {
//                                            expertTagMap[it!!.title] = it.userVerifiedCategoryId.toString()
//                                        }
//                                        it.expertCropType.data!!.filter { it?.name != null }.forEach {
//                                            cropTypeMap[it!!.name!!] = it.id.toString()
//                                        }
//                                        setTimeout(300L, {
//                                            openPopupWindow()
//                                        })
//                                    }, showError = {
//                                it.dismiss()
//                            })
//                }
//            })
        }
    }

    //    private var inflateDataBinding: ItemPopupFiltrateBinding? = null
    private var popupWindow: PopupWindow? = null

//    private fun openPopupWindow() {
//        //设置PopupWindow
//        val inflateDataBinding: ItemPopupFiltrateBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_popup_filtrate, null, false)
//        inflateDataBinding.setVariable(BR.background, viewBackgroundObs)
//        inflateDataBinding.setVariable(BR.textColor, viewTextColorObs)
//        inflateDataBinding.setVariable(BR.data, this@InvitationFragment)
//        inflateDataBinding.tvClear.setOnClickListener {
//            //初始化
//            expertTypeObs.set(null)
//            cropIdObs.set(null)
//            cityIdObs.set(null)
//            expertTempObs?.set(null)
//            cropTempObs?.set(null)
//            isSelectedDistrictObs.set(false)
//            previousTypeObs?.set(false)
//            previousCropObs?.set(false)
//            setTvClearBg()
//        }
//        popupWindow = PopupWindow(inflateDataBinding.root, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
//        popupWindow!!.isFocusable = true
//        popupWindow!!.isOutsideTouchable = true
//        popupWindow!!.animationStyle = R.style.PopupWindowTop
//        popupWindow!!.showAtLocation(inflateDataBinding.root, Gravity.TOP, 0, locationArray[1])
//        popupWindow!!.setOnDismissListener(this)
//        addHolder(inflateDataBinding)
////        inflateDataBinding.tvConfirm.setOnClickListener {
////            if (expertTypeObs.get() == null && cityIdObs.get() == null && cropIdObs.get() == null) {
////                toast("请选择筛选条件")
////                return@setOnClickListener
////            }
////            expertViewModel.operateExpertList(0, null, expertTypeObs.get(), cityIdObs.get(), null, cropIdObs.get())
////            popupWindow.dismiss()
////        }
//    }


    //    private lateinit var addressSelectDialog: AddressSelectDialog
    private fun addHolder(inflateDataBinding: QuestionItemPopupFiltrateBinding) {
        val adapter = setupRecyclerView(inflateDataBinding.recyclerView)
        val layoutHelpers = arrayListOf<LayoutHelper>()
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = 1

        val gridLayoutHelper1 = createGridLayoutHelper(3, expertTagMap.size)
        val gridLayoutHelper2 = createGridLayoutHelper(4, cropTypeMap.size)
        val gridLayoutHelper3 = createGridLayoutHelper(4, 2)

        layoutHelpers.add(linearLayoutHelper)
        layoutHelpers.add(gridLayoutHelper1)
        layoutHelpers.add(linearLayoutHelper)
        layoutHelpers.add(gridLayoutHelper2)
        layoutHelpers.add(linearLayoutHelper)
        layoutHelpers.add(gridLayoutHelper3)

        adapter.renderItems.add(TitleFiltrateHolder("专家类型:"))
        expertTagMap.forEach {
            val isSelectedTempObs = ObservableField(expertTypeObs.get() == it.value)
            if (isSelectedTempObs.get()!!) {
                expertTempObs = isSelectedTempObs
                previousTypeObs = isSelectedTempObs
            }
            adapter.renderItems.add(ExpertFiltrateHolder(ObservableField(it.key), isSelectedTempObs, it.value).apply {
                setOnClickListener {
                    isSelectedObs.set(true)
                    previousTypeObs?.set(false)
                    previousTypeObs = if ((previousTypeObs?.equals(isSelectedObs) == true)) {
                        expertTypeObs.set(null)
                        null
                    } else {
                        expertTypeObs.set(id)
                        isSelectedObs
                    }
                    setTvClearBg()
                }
            })
        }
        adapter.renderItems.add(TitleFiltrateHolder("专家擅长领域:"))
        cropTypeMap.forEach {
            val isSelectedTempObs = ObservableField(cropIdObs.get() == it.value)
            if (isSelectedTempObs.get()!!) {
                cropTempObs = isSelectedTempObs
                previousCropObs = isSelectedTempObs
            }
            adapter.renderItems.add(
                    ExpertFiltrateHolder(ObservableField(it.key), isSelectedTempObs, it.value).apply {
                        setOnClickListener {
                            isSelectedObs.set(true)
                            previousCropObs?.set(false)
                            previousCropObs = if (previousCropObs?.equals(isSelectedObs) == true) {
                                cropIdObs.set(null)
                                null
                            } else {
                                cropIdObs.set(id)
                                isSelectedObs
                            }
                            setTvClearBg()
                        }
                    }
            )
        }

        adapter.renderItems.add(TitleFiltrateHolder("专家地区:"))
        adapter.renderItems.add(ExpertFiltrateHolder(locationObs, isSelectedDistrictObs).apply {
            setOnClickListener {
                if (isSelectedDistrictObs.get()!!) {
                    isSelectedDistrictObs.set(false)
                    isSelectedCity = false
                    cityIdObs.set(null)
                } else {
                    isSelectedDistrictObs.set(true)
                    cityIdObs.set(cityIdObs.get())
                    isSelectedCity = true
                }
                setTvClearBg()
            }
        })
        adapter.renderItems.add(TitleFiltrateHolder("修改", true).apply {
            setOnClickListener {
                //                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(it.windowToken, 0)
//                addressSelectDialog = AddressSelectDialog.show(activity!!, OnAddressSelectedListener { province, city, county, _ ->
//                    locationObs.set(city?.name)
//                    cityIdObs.set(city?.id.toString())
//                    activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//                    isSelectedDistrictObs.set(true)
//                    isSelectedCity = true
//                    setTvClearBg()
//                    addressSelectDialog.dismiss()
//                }, false, isPickCity = true, isSuggestionLocation = false)

            }
        })

        adapter.layoutHelpers = layoutHelpers
    }


    //当筛选条件被选中,或者被取消 清除条件button状态改动
    private fun setTvClearBg() {
        if (isSelectedDistrictObs.get()!! || cropIdObs.get() != null || expertTypeObs.get() != null) {
            viewBackgroundObs.set(R.color.colorAccent)
            viewTextColorObs.set(ContextCompat.getColor(context!!, R.color.colorTextWhite))
            lookExpertObs.set("查看相关专家")
        } else {
            viewBackgroundObs.set(R.color.colorPageBgDark)
            viewTextColorObs.set(ContextCompat.getColor(context!!, R.color.colorTextDarkWeek))
            lookExpertObs.set("查看全部专家")

        }
    }

    fun onLookExpert(it: View) {
        if (lookExpertObs.get() == "查看相关专家") {
            val tempCityId: String? = if (isSelectedCity) cityIdObs.get() else null
            expertViewModel.operateExpertList(0, isFollow, expertTypeObs.get(), tempCityId, null, cropIdObs.get())
            popupWindow?.dismiss()
        } else {
            expertViewModel.operateExpertList(0, isFollow, null, null, null, null)
            popupWindow?.dismiss()
        }

    }

    override fun onDismiss() {
        popupWindow?.dismiss()
    }

    private fun createGridLayoutHelper(spanCount: Int, itemCount: Int): GridLayoutHelper {
        val gridLayoutHelper = GridLayoutHelper(spanCount, itemCount)
        gridLayoutHelper.setAutoExpand(false)
        gridLayoutHelper.hGap = 24.dp()
        gridLayoutHelper.vGap = 16.dp()
        gridLayoutHelper.setMargin(0, 8.dp(), 0, 8.dp())
        return gridLayoutHelper
    }

//    private fun getLocation() {
//        listener = object : LocationListener {
//            override fun onLocationChanged(p0: Location?) {
//                if (p0 != null)
//                    locationRepository.getLocationAddressPegging(p0.latitude, p0.longitude) { city: String, province: String, _ ->
//                        apiProvider.create(UserApi::class.java)
//                                .getSuggestAddress(province, city.substring(0, city.length - 1))
//                                .mainIoSchedulers()
//                                .subscribe({
//                                    it.content?.forEach {
//                                        if (it.regionType == 2) {
//                                            cityIdObs.set(it.regionId)
//                                            locationObs.set(it.regionName)
//                                        }
//                                    }
//                                }, {})
//                    }
//            }
//
//            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//            }
//
//            override fun onProviderEnabled(p0: String?) {
//            }
//
//            override fun onProviderDisabled(p0: String?) {
//            }
//        }
//        locationRepository.getLocation(activity as InvitationActivity, listener!!, locationManager)
//    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null) {
            showNeedOauth()
        } else {
            onReload()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (listener != null)
            locationManager.removeUpdates(listener)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshExpertEvent: RefreshExpertEvent) {
        if (isFollow && refreshExpertEvent.isRefresh)
            onReload()
    }
}
