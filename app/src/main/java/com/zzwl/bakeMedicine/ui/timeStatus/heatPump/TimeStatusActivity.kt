package com.zzwl.bakeMedicine.ui.timeStatus.heatPump

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.Status
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.g.base.utils.PopupWindowUtils
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewFilterQuestionBinding
import com.zzwl.bakeMedicine.databinding.ViewSwiperefreshRecyclerviewBinding
import com.zzwl.bakeMedicine.event.RefreshTobaccoSelectedEvent
import com.zzwl.bakeMedicine.room.entity.remote.HotPumpTobaccoStatusEntity
import com.zzwl.bakeMedicine.room.entity.remote.HouseInfo
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder.FilterHolder
import com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder.HotPumpHolder
import com.zzwl.bakeMedicine.ui.timeStatus.holder.TobaccoHeadHolder
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPage.HotPumpTimeStatusActivity)
class TimeStatusActivity : BaseActivity<ViewSwiperefreshRecyclerviewBinding>() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(TobaccoStatisticsViewModel::class.java) }
    private var isFirst: Boolean = true//是否第一次进入界面
    private val selectedObs = ObservableField<Int?>(0)  //被选中的烤房
    private lateinit var tobaccoHeadHolder: TobaccoHeadHolder
    private lateinit var hotPumpHolder: HotPumpHolder
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_swiperefresh_recyclerview)
        obsHouseListDate()
        obsHouseStatus()
        onReload()
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        toolbar.title = "热泵烤房"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        contentView.refreshLayout.setOnRefreshListener {
            onReload()
        }
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_add_tobacco)
        val layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        toolbar.addView(imageView, layoutParams)
        imageView.setOnClickListener {
            openPopupWindow(imageView)
        }

        contentView.recyclerView2.setBackgroundColor(Color.WHITE)
    }

    private fun openPopupWindow(imageView: ImageView) {
        val inflateDataBinding = DataBindingUtil.inflate<ViewFilterQuestionBinding>(LayoutInflater.from(this), R.layout.view_filter_question, null, false)
        val adapter = setupRecyclerView(inflateDataBinding.recyclerView)
        val array = arrayOf("输出状态", "温度状态")
        val popupWindow = PopupWindowUtils.build(
                inflateDataBinding.root,
                imageView,
                72.dp(),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                6.dp(), 16.dp(), Gravity.RIGHT)
        adapter.renderItems.addAll(array.map {
            FilterHolder(it).apply {
                setOnClickListener {
                    when (item) {
                        "输出状态" -> {
                            ARouter.getInstance().build(RouterPage.OutputActivity).navigation(this@TimeStatusActivity)
                        }
                        "温度状态" -> {
                            ARouter.getInstance().build(RouterPage.TempActivity).navigation(this@TimeStatusActivity)
                        }

                    }
                    popupWindow.dismiss()
                }
            }
        })
        adapter.notifyDataSetChanged()
    }

    override fun onReload() {
        viewModel.isRefreshAll = true
        viewModel.setGroupLiveData(getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1))
    }

    private fun obsHouseListDate() {
        viewModel.getTobaccoStatistics()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Content -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            if (it.data!!.houseInfoList.isNotEmpty()) {
                                if (isFirst) {
                                    selectedObs.set(it.data!!.houseInfoList[0].houseId)
                                    isFirst = false
                                } else {
                                    if (it.data!!.houseInfoList.firstOrNull({ it.houseId == selectedObs.get()!! }) == null)
                                        selectedObs.set(it.data!!.houseInfoList[0].houseId)
                                }
                                tobaccoHeadHolder = TobaccoHeadHolder(it.data!!.houseInfoList as ArrayList<HouseInfo>,
                                        selectedObs,
                                        viewModel).apply {
                                    setOnClickListener {
                                        ARouter.getInstance().build(RouterPage.AllTobaccoActivity).withInt("selectedId", selectIdObs.get()
                                                ?: 0).navigation(this@TimeStatusActivity)
                                    }
                                }

                                viewModel.setHotPumpHouseLiveData(selectedObs.get()!!)

                            }
                        }
                        Status.Error -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            showError(it.error?.message)
                        }
                    }
                })
    }

    private fun obsHouseStatus() {
        viewModel.getHotPumpTobaccoStatus()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {

                        Status.Content -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            initTobaccoData(it.data!!)
                        }
                        Status.Error -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            toast(it.error?.message ?: "加载失败,请刷新重试")
                            initTobaccoData(HotPumpTobaccoStatusEntity())
                        }
                    }
                })

    }

    private fun initTobaccoData(data: HotPumpTobaccoStatusEntity) {
        hotPumpHolder = HotPumpHolder(
                ObservableField(data.machineStatus.toString()),
                ObservableField(data.workStatus.toString()),
                ObservableField(data.currentFormula.toString()),
                ObservableField(data.currentNumber.toString()),
                ObservableField(data.currentRunningTime.toString()),
                ObservableField(data.totalTime.toString()),
                ObservableField(data.remainingTime.toString()),
                ObservableField(data.currentTemp.toString()),
                ObservableField(data.setTemp.toString()),
                ObservableField(data.currentWetness.toString()),
                ObservableField(data.setWetness.toString())
        )
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter.renderItems.clear()
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.marginBottom = 24.dp()
        linearLayoutHelper.setDividerHeight(3.dp())
        linearLayoutHelper.itemCount = 3
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.renderItems.add(tobaccoHeadHolder)
        adapter.renderItems.add(hotPumpHolder)
//        adapter.renderItems.add(tobaccoTailHolder)
        if (viewModel.isRefreshAll)
            adapter.notifyDataSetChanged()
        else {
            adapter.notifyItemRangeChanged(1, 2)
            viewModel.isRefreshAll = true
        }
        showContentView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(refreshTobaccoSelectedEvent: RefreshTobaccoSelectedEvent) {
        if (refreshTobaccoSelectedEvent.isRefresh) {
            selectedObs.set(refreshTobaccoSelectedEvent.id)
            viewModel.setHouseLiveData(refreshTobaccoSelectedEvent.id)
        }
    }
}