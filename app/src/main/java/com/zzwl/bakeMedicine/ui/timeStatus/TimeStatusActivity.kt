package com.zzwl.bakeMedicine.ui.timeStatus

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.*
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewSwiperefreshRecyclerviewBinding
import com.zzwl.bakeMedicine.event.CurveEvent
import com.zzwl.bakeMedicine.event.RefreshTobaccoSelectedEvent
import com.zzwl.bakeMedicine.room.entity.remote.HouseInfo
import com.zzwl.bakeMedicine.room.entity.remote.TobaccoCurveEntity
import com.zzwl.bakeMedicine.room.entity.remote.TobaccoStatusEntity
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.timeStatus.holder.TobaccoContentHolder
import com.zzwl.bakeMedicine.ui.timeStatus.holder.TobaccoHeadHolder
import com.zzwl.bakeMedicine.ui.timeStatus.holder.TobaccoTailHolder
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPage.TimeStatusActivity, extras = RouteExtras.NeedOauth)
class TimeStatusActivity : BaseActivity<ViewSwiperefreshRecyclerviewBinding>() {
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    private lateinit var tobaccoHeadHolder: TobaccoHeadHolder
    private lateinit var tobaccoContentHolder: TobaccoContentHolder
    private lateinit var tobaccoTailHolder: TobaccoTailHolder
    private var dryBulbTemp: Float = 0f
    private var wetBulbTemp: Float = 0f
    private var workStep: Float = 0f
    private var leaf: Int = 0
    private var isKeepingTmp: Boolean = false
    override var hasToolbar: Boolean = true
    private val viewModel by lazy { ViewModelProviders.of(this).get(TobaccoStatisticsViewModel::class.java) }
    private val selectedObs = ObservableField<Int?>(0)  //被选中的烤房
    private val dryTempList by lazy { arrayListOf(34f, 36f, 38f, 40f, 42f, 44f, 47f, 50f, 53f, 57f, 61f, 65f) }//曲线为固定值,
    private val wetTempList by lazy { arrayListOf(24f, 26f, 28f, 30f, 32f, 34f, 36f, 38f, 40f, 42f, 44f, 46f) }//曲线为固定值,
    private var isFirst: Boolean = true//是否第一次进入界面
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_swiperefresh_recyclerview)
        obsHouseListDate()
        obsHouseStatus()
        onReload()
        initView()
    }

    private fun initView() {
        toolbar.title = "实时状态"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        contentView.refreshLayout.setOnRefreshListener {
            onReload()
        }
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

                                viewModel.setHouseLiveData(selectedObs.get()!!)

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
        viewModel.getTobaccoStatus()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {

                        Status.Content -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            initTobaccoData(it.data!!)
                            getCurveData(selectedObs.get()!!)
                        }
                        Status.Error -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            toast(it.error?.message ?: "加载失败,请刷新重试")
                            initTobaccoData(TobaccoStatusEntity())
                            getCurveData(selectedObs.get()!!)
                        }
                    }
                })

    }


    private fun getCurveData(houseId: Int) {
        viewModel.getTobaccoCurve(houseId, leaf)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Content -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false

                            initLine(it.data!!)
                            initRecyclerView()
                            showContentView()
                        }
                        Status.Error -> {
                            if (contentView.refreshLayout.isRefreshing)
                                contentView.refreshLayout.isRefreshing = false
                            initLine(listOf(TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity(),
                                    TobaccoCurveEntity()))
                            initRecyclerView()
                            showContentView()
                        }

                    }

                })
    }

    private fun initTobaccoData(data: TobaccoStatusEntity) {
        dryBulbTemp = data.dryBulbTemp
        wetBulbTemp = data.wetBulbTemp
        workStep = data.workStep
        leaf = data.leaf
        val tempFan = when (data.fanStatus) {
            0 -> "停止"
            1 -> "自动"
            2 -> "低速"
            else -> "高速"
        }
        val tempRelativeHumid = if (data.relativeHumidity.length > 5)
            data.relativeHumidity.substring(0, 5)
        else
            data.relativeHumidity
        isKeepingTmp = data.temperatureStatus == 1//0,1:升温,恒温
        tobaccoContentHolder = TobaccoContentHolder(
                ObservableField(data.combustionOutputStatus == 0),
                ObservableField(data.arefactionStatus == 0),
                ObservableField("风机：$tempFan"),
                ObservableField(data.fanStatus == 0),
                ObservableField(data.iconKey == 0),
                ObservableField(data.iconTrumpet == 0),
                ObservableField(data.iconStandbyCell == 0),
                ObservableField(data.iconFrequency == 0),
                ObservableField(data.iconNetwork == 0),
                ObservableField(data.isPartialTemperature),
                ObservableField(data.isPhaseLoss || data.isOverload),
                ObservableField(data.dryBulbTemp),
                ObservableField(data.dryBulbGoalTemp),
                ObservableField(data.wetBulbTemp),
                ObservableField(data.wetBulbGoalTemp),
                ObservableField(data.temperatureStatus == 1),
                ObservableField(tempRelativeHumid),
                ObservableField(data.theOtherDryBulbTemp),
                ObservableField(data.theOtherWetBulbTemp),
                ObservableField(data.workStepTime),
                ObservableField(data.workTotalTime),
                ObservableField(data.powerVoltage),
                ObservableField(data.workStep.toInt()),
                ObservableField(data.controlShed == 0)
        )
        showContentView()
    }

    private fun initRecyclerView() {
        adapter.renderItems.clear()
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.marginBottom = 24.dp()
        linearLayoutHelper.setDividerHeight(3.dp())
        linearLayoutHelper.itemCount = 3
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.renderItems.add(tobaccoHeadHolder)
        adapter.renderItems.add(tobaccoContentHolder)
        adapter.renderItems.add(tobaccoTailHolder)
        if (viewModel.isRefreshAll)
            adapter.notifyDataSetChanged()
        else {
            adapter.notifyItemRangeChanged(1, 2)
            viewModel.isRefreshAll = true
        }
    }

    private fun initLine(data: List<TobaccoCurveEntity>) {
        //创建线条
        val dryGoalEntryList = ArrayList<Entry>()       //干球预设曲线
        val wetGoalEntryList = ArrayList<Entry>()       //湿球预设曲线
        val wetGoalEntryList2 = ArrayList<Entry>()       //湿球预设时间曲线
        val offsetTemp = if (isKeepingTmp) -0.5f else   -1.5f   //点或者线段的X偏移量
        if (workStep == 0f) workStep = 1f // 如果烤房停用阶段为0,而正常运行的烤房阶段为1~10 为了描点正确,workstep为0时设置成1
        val dryBulbTempEntryList = arrayListOf(Entry(workStep * 2 + offsetTemp, dryBulbTemp, TextData("干球实时温度: ", "$dryBulbTemp", "℃")))    //干球实时温度
        val wetBulbTempEntryList = arrayListOf(Entry(workStep * 2 + offsetTemp, wetBulbTemp, TextData("湿球实时温度: ", "$wetBulbTemp", "℃")))    //湿球实时温度
        val maxTemp: Float
        val minTemp: Float
        if (dryBulbTemp > wetBulbTemp) {
            maxTemp = dryBulbTemp
            minTemp = wetBulbTemp
        } else {
            maxTemp = wetBulbTemp
            minTemp = dryBulbTemp
        }
        val auxiliaryEntryList = arrayListOf(Entry(workStep * 2 + offsetTemp, maxTemp + 20f, TextData()), Entry(workStep * 2 + offsetTemp, minTemp - 20f, TextData()))      //辅助线
        data.forEachIndexed { index, tobaccoCurveEntity ->

            val x = index * 2.toFloat()
            val x1 = (index * 2 + 1).toFloat()

            dryGoalEntryList.add(Entry(x, dryTempList[index], TextData()))
            dryGoalEntryList.add(Entry(x1, dryTempList[index + 1], TextData("干球目标温度: ", "${tobaccoCurveEntity.dryBulbGoalTemp.toInt()}", "℃")))
            if (index == data.size - 1) {
                dryGoalEntryList.add(Entry(x1 + 1, dryTempList[index + 1], TextData()))
            }

            wetGoalEntryList.add(Entry(x, wetTempList[index], TextData()))
            wetGoalEntryList.add(Entry(x1, wetTempList[index + 1], TextData("湿球目标温度: ", "${tobaccoCurveEntity.wetBulbGoalTemp}", "℃")))
            if (index == data.size - 1) {
                wetGoalEntryList.add(Entry(x1 + 1, wetTempList[index + 1], TextData()))
            }
            val temp = if (tobaccoCurveEntity.tempHeatingTime == 0f) "" else tobaccoCurveEntity.tempHeatingTime.toInt().toString()
            wetGoalEntryList2.add(Entry(x + 0.5f, wetTempList[index] - 4f, TextData(value = temp)))
            wetGoalEntryList2.add(Entry(x1 + 0.5f, wetTempList[index] - 4f, TextData(value = tobaccoCurveEntity.tempHoldingTime.toInt().toString())))
        }


        val dryLineDataSet = LineDataSet(dryGoalEntryList, "干球设定℃")
        val wetLineDataSet = LineDataSet(wetGoalEntryList, "湿球设定℃")
        val wetLineDataSet2 = LineDataSet(wetGoalEntryList2, null)
        val dryTempLineDataSet = LineDataSet(dryBulbTempEntryList, null)
        val wetTempLineDataSet = LineDataSet(wetBulbTempEntryList, null)
        val auxiliaryLineDataSet = LineDataSet(auxiliaryEntryList, null)

        dryLineDataSet.color = ContextCompat.getColor(this, R.color.colorChartRed)
        dryLineDataSet.setDrawCircles(false)
        dryLineDataSet.valueFormatter = TempValueFormatter()

        wetLineDataSet.color = ContextCompat.getColor(this, R.color.colorChartBlue)
        wetLineDataSet.setDrawCircles(false)
        wetLineDataSet.valueFormatter = TempValueFormatter()

        wetLineDataSet2.color = ContextCompat.getColor(this, R.color.colorTransparent)
        wetLineDataSet2.setDrawCircles(false)
        wetLineDataSet2.valueFormatter = TempValueFormatter()

        dryTempLineDataSet.circleHoleRadius = 4f
        dryTempLineDataSet.circleRadius = 5f
        dryTempLineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorChartRedWeek))
        dryTempLineDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.colorChartRed))
        dryTempLineDataSet.valueFormatter = TempValueFormatter()

        wetTempLineDataSet.circleHoleRadius = 4f
        wetTempLineDataSet.circleRadius = 5f
        wetTempLineDataSet.setDrawHighlightIndicators(true)
        wetTempLineDataSet.setDrawHorizontalHighlightIndicator(false)
        wetTempLineDataSet.setDrawVerticalHighlightIndicator(true)
        wetTempLineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorChartBlueWeek))
        wetTempLineDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.colorChartBlue))
        wetTempLineDataSet.valueFormatter = TempValueFormatter()


        auxiliaryLineDataSet.color = ContextCompat.getColor(this, R.color.colorChartAuxiliary)
        auxiliaryLineDataSet.valueFormatter = TempValueFormatter()
        auxiliaryLineDataSet.setDrawCircles(false)

        tobaccoTailHolder = TobaccoTailHolder(
                dryLineDataSet,
                wetLineDataSet,
                dryTempLineDataSet,
                wetTempLineDataSet,
                auxiliaryLineDataSet,
                wetLineDataSet2,
                leaf
        ).apply {
            setOnClickListener {
                when (it.id) {
                    R.id.tvHistory -> ARouter.getInstance().build(RouterPage.HistoryCurveActivity).withInt("houseId", selectedObs.get()!!).navigation(this@TimeStatusActivity)
                    R.id.imgLandscape -> {
                        EventBus.getDefault().postSticky(CurveEvent(
                                dryLineDataSet,
                                wetLineDataSet,
                                dryTempLineDataSet,
                                wetTempLineDataSet,
                                auxiliaryLineDataSet,
                                wetLineDataSet2,
                                leaf,
                                isKeepingTmp
                        ))
                        ARouter.getInstance().build(RouterPage.LandscapeActivity).navigation(this@TimeStatusActivity)
                    }
                    R.id.tvSetting -> {
                        ARouter.getInstance().build(RouterPage.CurveSettingActivity)
                                .withInt("leaf", leaf)
                                .withInt("houseId", selectedObs.get()!!).navigation(this@TimeStatusActivity)
                    }
                }

            }

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

