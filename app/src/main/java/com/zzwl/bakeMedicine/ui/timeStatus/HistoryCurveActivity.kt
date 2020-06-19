package com.zzwl.bakeMedicine.ui.timeStatus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.help.ProgressDialog
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityHistoryCurveBinding
import com.zzwl.bakeMedicine.databinding.ViewRecyclerviewBinding
import com.zzwl.bakeMedicine.room.entity.remote.HistoryCurveDataEntity
import com.zzwl.bakeMedicine.room.entity.remote.PullDownDataEntity
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.timeStatus.holder.PullDownHolder
import com.zzwl.bakeMedicine.viewModel.HistoryCurveViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Route(path = RouterPage.HistoryCurveActivity, extras = RouteExtras.NeedOauth)
class HistoryCurveActivity : BaseActivity<ActivityHistoryCurveBinding>() {
    @Autowired
    @JvmField
    var houseId: Int = 0

    override var isFullScreen: Boolean = true
    override var isPortrait: Boolean = false
    private val viewModel by lazy { ViewModelProviders.of(this).get(HistoryCurveViewModel::class.java) }
    private val xAxisValueMap = hashMapOf<Float, String>()      //x轴的值
    val dateFromObs by lazy { ObservableField("请选择烤次") }
    private var pullDownDataId = 0
    private val dryGoalEntryList = ArrayList<Entry>()       //干球预设曲线数据
    private val wetGoalEntryList = ArrayList<Entry>()       //湿球预设曲线数据
    private val dryEntryList = ArrayList<Entry>()           //干球曲线数据
    private val wetEntryList = ArrayList<Entry>()           //湿球曲线数据
    private var lastCreateTime = 0L
    private val builder by lazy { AlertDialog.Builder(this) }
    private val dialog by lazy { builder.create() }
    private val pullDownList by lazy { ArrayList<PullDownDataEntity>() }
    private val progressDialog by lazy { ProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.activity_history_curve)
        contentView.data = this
        contentView.lineChart.setNoDataText("暂无数据")
        showContentView()
        initView()
        getPullDownData()
    }

    private fun initView() {
        contentView.tvDateFrom.setOnClickListener {
            openDatePickerDialog()
        }

        contentView.imgBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun openDatePickerDialog() {
        val inflateDataBinding = DataBindingUtil.inflate<ViewRecyclerviewBinding>(LayoutInflater.from(this), R.layout.view_recyclerview, null, false)
        dialog.setView(inflateDataBinding.root)
        val adapter = setupRecyclerView(inflateDataBinding.recyclerView)
        pullDownList.forEach {
            val tempEndDate = if (it.endTime.isEmpty()) {
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                simpleDateFormat.format(Date(System.currentTimeMillis()))
            } else
                it.endTime.substring(0, 10)
            adapter.renderItems.add(PullDownHolder("第${it.times}烤次   " + it.startTime.substring(0, 10) + "-" + tempEndDate, it.id).apply {
                setOnClickListener {
                    dateFromObs.set(text)
                    pullDownDataId = id
                    getData()
                    dialog.dismiss()
                }
            })
        }
        adapter.notifyDataSetChanged()
        dialog.show()


    }

    @SuppressLint("SimpleDateFormat")
    private fun getPullDownData() {
        progressDialog.setStart("正在努力的加载中..")
        viewModel.getPullDownData(houseId)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Content -> {
                            pullDownList.clear()
                            pullDownList.addAll(it.data!! as ArrayList)
                            if (pullDownList.isNotEmpty()) {
                                val tempEndDate = if (pullDownList[0].endTime.isEmpty()) {
                                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                                    simpleDateFormat.format(Date(System.currentTimeMillis()))
                                } else
                                    pullDownList[0].endTime.substring(0, 10)
                                dateFromObs.set("第${pullDownList[0].times}烤次   " + pullDownList[0].startTime.substring(0, 10) + "-" + tempEndDate)
                            }
                            pullDownDataId = pullDownList[0].id
                            getData()
                        }
                        Status.Error -> {
                            progressDialog.setFiled(it.error?.message ?: "没有相关数据") { it.dismiss() }
                        }
                        Status.Oauth -> {
                            progressDialog.setFiled(it.error?.message
                                    ?: "登录失效请重新登录") { it.dismiss() }
                        }
                    }
                })

    }

    private fun getData() {
        if (!progressDialog.isLoading)
            progressDialog.setStart("正在努力的加载中..")
        viewModel.getHistoryCurveData(pullDownDataId)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Content -> {
                            initCurve(it.data!!)
                            showContentView()
                            if (progressDialog.isLoading)
                                progressDialog.dismiss()
                        }
                        Status.Error -> {
                            contentView.lineChart.clear()
                            if (progressDialog.isLoading)
                                progressDialog.setFiled(it.error?.message
                                        ?: "没有相关数据") { it.dismiss() }
                            showContentView()
                        }
                        Status.Oauth -> {
                            if (progressDialog.isLoading)
                                progressDialog.setFiled(it.error?.message
                                        ?: "登录失效请重新登录") { it.dismiss() }
                        }
                    }
                })
    }

    @SuppressLint("SimpleDateFormat")
    private fun initCurve(data: List<HistoryCurveDataEntity>) {
        if (dryGoalEntryList.isNotEmpty()) dryGoalEntryList.clear()
        if (wetGoalEntryList.isNotEmpty()) wetGoalEntryList.clear()
        if (dryEntryList.isNotEmpty()) dryEntryList.clear()
        if (wetEntryList.isNotEmpty()) wetEntryList.clear()

        data.forEachIndexed { index, historyCurveDataEntity ->
            //x坐标取差值,第一个设置x坐标为零,
            val i = index.toFloat()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val createTime = simpleDateFormat.parse(data[index].createTime).time

            lastCreateTime = createTime
            dryGoalEntryList.add(Entry(i, historyCurveDataEntity.dryBulbGoalTemp))
            wetGoalEntryList.add(Entry(i, historyCurveDataEntity.wetBulbGoalTemp))
            dryEntryList.add(Entry(i, historyCurveDataEntity.dryBulbTemp))
            wetEntryList.add(Entry(i, historyCurveDataEntity.wetBulbTemp))
            xAxisValueMap[i] = historyCurveDataEntity.createTime
        }


        val dryGoalDataSet = LineDataSet(dryGoalEntryList, null)
        val wetGoalDataSet = LineDataSet(wetGoalEntryList, null)
        val dryDataSet = LineDataSet(dryEntryList, null)
        val wetDataSet = LineDataSet(wetEntryList, null)

        dryGoalDataSet.color = ContextCompat.getColor(this, R.color.colorChartRed)
        dryGoalDataSet.setDrawCircles(false)

        wetGoalDataSet.color = ContextCompat.getColor(this, R.color.colorChartSkyBlue)
        wetGoalDataSet.setDrawCircles(false)

        dryDataSet.color = ContextCompat.getColor(this, R.color.colorChartRedDark)
        dryDataSet.setDrawCircles(false)

        wetDataSet.color = ContextCompat.getColor(this, R.color.colorChartCyan)
        wetDataSet.setDrawCircles(false)
        initAxis(dryGoalDataSet, wetGoalDataSet, dryDataSet, wetDataSet)
    }

    private fun initAxis(
            dryGoalDataSet: LineDataSet,
            wetGoalDataSet: LineDataSet,
            dryDataSet: LineDataSet,
            wetDataSet: LineDataSet) {
        val lineData = LineData(dryGoalDataSet, wetGoalDataSet, dryDataSet, wetDataSet)
        val xAxis = contentView.lineChart.xAxis
        val axisLeft = contentView.lineChart.axisLeft
        val axisRight = contentView.lineChart.axisRight

//        xAxis.isEnabled = false     //去掉x轴
//        axisLeft.isEnabled = false  //去掉左y轴
        axisRight.isEnabled = false //去掉右y轴
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = TimeValueFormatter(xAxisValueMap)
        lineData.setValueTextColor(ContextCompat.getColor(this, R.color.colorTextWeek))
        lineData.setValueTextSize(10f)                          //数字大小
        contentView.lineChart.legend.isEnabled = false          //去掉左下角描述
        val marker: IMarker = CustomMarkView(this, R.layout.layout_mark)
        contentView.lineChart.marker = marker                   //点击事件
        contentView.lineChart.description = Description().apply { text = "" }//去掉右下角的描述
//        contentView.lineChart.setTouchEnabled(false)           //禁止全部手势交互
        contentView.lineChart.setPinchZoom(true)
        contentView.lineChart.data = lineData                  //设置数据
        contentView.lineChart.invalidate()                     //刷新view

    }

    override fun onReload() {
        getPullDownData()
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data != null)
            onReload()
        else
            showNeedOauth()
    }

}