package com.zzwl.bakeMedicine.ui.home.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.help.tryCatch
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.Status
import com.g.base.token.TokenManage
import com.g.base.ui.BaseFragment
import com.g.base.utils.DialogUtils
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.api.UserApi
import com.zzwl.bakeMedicine.databinding.FragmentHomeBinding
import com.zzwl.bakeMedicine.databinding.ViewToolbarScanBinding
import com.zzwl.bakeMedicine.event.RefreshHomeEvent
import com.zzwl.bakeMedicine.event.ScanResultEvent
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.home.HomeActivity
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.concurrent.TimeUnit


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override var hasStatusBar: Boolean = true
    override var hasToolbar: Boolean = true
    override fun setContentView(): Int = R.layout.fragment_home
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TobaccoStatisticsViewModel::class.java) }
    private val textViewList by lazy { ArrayList<TextView>() }
    val runningNumObs by lazy { ObservableField(0) }
    val faultNumObs by lazy { ObservableField(0) }
    val stopNumObs by lazy { ObservableField(0) }
    val tobaccoStatusObs by lazy { ObservableField("") }
    private val textList by lazy { listOf("精准烘烤正式上线啦!!", "随时随地查看您的烤房信息。", "全新版本，全新体验。", "精准烘烤，您身边的烘烤管家。") }
    private var groupId: Int = -1
    private val viewScan by lazy { toolbar.inflateDataBinding<ViewToolbarScanBinding>(R.layout.view_toolbar_scan) }
    private val weatherFragmentPagerItem by lazy { FragmentPagerItem.of("page", WeatherFragment::class.java) }
    override fun onLazyLoadOnce() {
        groupId = context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1)
        contentView.data = this
        obsDate()
        initView()
        contentView.swipeRefreshLayout.isRefreshing = true
        if (groupId != -1)
            onReload()
        else {
            contentView.swipeRefreshLayout.isRefreshing = false
            showContentView()
        }
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        initViewPager()
        val layoutParams = viewScan.imgScan.layoutParams as Toolbar.LayoutParams
        layoutParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        viewScan.imgScan.layoutParams = layoutParams
        toolbar.addView(viewScan.imgScan)
        viewScan.imgScan.setOnClickListener {
            startScanActivity()
        }

        addTextView()
        setOnclickListener()
    }

    //初始化viewpager
    @SuppressLint("CheckResult")
    private fun initViewPager() {
        var viewPageNum = 0
        var isSlideAble = true //此时是否可以滑动
        val pages = FragmentPagerItems(context)
        pages.add(weatherFragmentPagerItem)
        for (i in 0..3) {
            pages.add(FragmentPagerItem.of("page", ViewPagerFragment::class.java, Bundle().apply {
                putInt("picture", i)
            }))
        }
        contentView.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                isSlideAble = state != 1

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewPageNum = position
            }
        })
        val adapter = FragmentPagerItemAdapter(childFragmentManager, pages)
        contentView.viewPager.adapter = adapter
        contentView.viewPager.offscreenPageLimit = pages.size
        adapter.getItem(0)
        contentView.viewpagertab.setViewPager(contentView.viewPager)
        Observable.interval(5, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (isSlideAble) {
                        if (viewPageNum == pages.size) viewPageNum = 0
                        contentView.viewPager.currentItem = viewPageNum++
                    }
                }
    }


    private fun openDialog() {
        val message: String
        val type: Int //0,1:选择烤房群,绑定烤房群
        if ((TokenManage.getToken()?.groupIdList?.size ?: 0) > 2) {
            message = "您还没有选择烤房群,  是否前去选择?"
            type = 0
        } else {
            message = "你还没有绑定烤房群,  是否前去绑定?"
            type = 1
        }

        DialogUtils.build(activity!!, msg = message, onPositive = {
            if (type == 0) {
                ARouter.getInstance().build(RouterPage.MapActivity).navigation(context)
            } else {
                startScanActivity()
            }
        }).show()

    }

    @SuppressLint("CheckResult")
    override fun onReload() {
        groupId = context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1)
        viewModel.setGroupLiveData(groupId)
    }

    private fun obsDate() {
        viewModel.getTobaccoStatistics()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
                            if (contentView.swipeRefreshLayout.isRefreshing)
                                contentView.swipeRefreshLayout.isRefreshing = false
                            initData(it.data!!.name, it.data!!.normalCount, it.data!!.alarmCount, it.data!!.stopCount, "${it.data!!.regionAddress}${it.data!!.addressDetail}")
                            showContentView()
                        }
                        Status.Error -> {
                            if (contentView.swipeRefreshLayout.isRefreshing)
                                contentView.swipeRefreshLayout.isRefreshing = false
                            toast(it.error?.message ?: "加载失败,请刷新重试")
                            initData("", 0, 0, 0, "")
                            showContentView()
                        }
                    }
                }
    }

    private fun initData(title: String, runningNum: Int, faultNum: Int, stopNum: Int, tobaccoStatus: String) {
        toolbar.title = title
        runningNumObs.set(runningNum)
        faultNumObs.set(faultNum)
        stopNumObs.set(stopNum)
        tobaccoStatusObs.set(tobaccoStatus)
    }

    //公告
    private fun addTextView() {
        if (textViewList.size != 0)
            textViewList.clear()
        textList.forEach {
            val textView = TextView(context)
            textView.textSize = 5.dp().toFloat()
            textView.setTextColor(ContextCompat.getColor(context!!, R.color.colorTextDarkWeek))
            textView.text = it
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setSingleLine()
            textView.ellipsize = TextUtils.TruncateAt.valueOf("END")
            textView.setOnClickListener {
            }
            textViewList.add(textView)
        }
        textViewList.forEach {
            contentView.viewFlipper.addView(it)
            contentView.viewFlipper.setInAnimation(contentView.root.context!!, R.anim.push_up_in)
            contentView.viewFlipper.setOutAnimation(contentView.root.context!!, R.anim.push_up_out)
            contentView.viewFlipper.startFlipping()
        }
    }

    @SuppressLint("CheckResult")
    private fun startScanActivity() {
        RxPermissions(activity as HomeActivity).request(Manifest.permission.CAMERA)
                .subscribe(
                        {
                            if (it) {
                                ARouter.getInstance().build(RouterPage.ScanActivity).navigation(activity)

                            } else {
                                toast("获取照相机权限失败,请前往设置中心授权")
                            }
                        },
                        {
                            toast("获取照相机权限失败,请前往设置中心授权")
                        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshHomeEvent) {
        if (event.isRefresh) {
            tryCatch {
                groupId = event.groupId.toInt()
            }
            onReload()
        }
    }

    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanEvent(scanResultEvent: ScanResultEvent) {
        val tempString = scanResultEvent.result.split("id=")
        var tempGroupId = "-1"
        var paramsId = -1
        if (tempString.size > 1)
            tempGroupId = tempString[1]
        else {
            toast("请扫描烤烟群的二维码")
            return
        }
        try {
            paramsId = tempGroupId.toInt()
        } catch (error: Exception) {
            toast("请扫描烤烟群的二维码")
            return
        }
        apiProvider.create(UserApi::class.java)
                .joinHouseGroup(paramsId)
                .mainIoSchedulers()
                .subscribe({
                    groupId = paramsId
                    context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", groupId).apply()
                    onReload()
                    toast("绑定烤房群 ${it.content?.name} 成功")
                }, { toast(it.message ?: "绑定烤房群失败~") })

    }

    private fun setOnclickListener() {
        contentView.viewStatus.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else {
                ARouter.getInstance().build(RouterPage.HotPumpTimeStatusActivity).navigation(context)//todo
            }
        }
        contentView.viewWarning.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else
                ARouter.getInstance().build(RouterPage.WarningHistoryActivity).navigation(context)
        }
        contentView.viewInfo.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else
                ARouter.getInstance().build(RouterPage.TobaccoAdminActivity).navigation(context)
        }
        contentView.viewMap.setOnClickListener {
            ARouter.getInstance().build(RouterPage.MapActivity).navigation(context)
        }
        contentView.swipeRefreshLayout.setOnRefreshListener {
            groupId = context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1)
            onReload()
        }
        contentView.viewRunning.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else
                ARouter.getInstance().build(RouterPage.AllTobaccoActivity).withInt("type", 0).withBoolean("isClickAble", false).navigation(context)
        }
        contentView.viewFault.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else
                ARouter.getInstance().build(RouterPage.AllTobaccoActivity).withInt("type", 1).withBoolean("isClickAble", false).navigation(context)
        }
        contentView.viewStop.setOnClickListener {
            if (groupId == -1) {
                openDialog()
            } else
                ARouter.getInstance().build(RouterPage.AllTobaccoActivity).withInt("type", 2).withBoolean("isClickAble", false).navigation(context)
        }
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data != null)
            onReload()
    }
}