package com.zzwl.bakeMedicine.ui.timeStatus

import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityLandscapeBinding
import com.zzwl.bakeMedicine.event.CurveEvent
import com.zzwl.bakeMedicine.router.RouterPage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPage.LandscapeActivity, extras = RouteExtras.NeedOauth)
class LandscapeActivity : BaseActivity<ActivityLandscapeBinding>() {
    override var isPortrait: Boolean = false
    val leafObs by lazy { ObservableField(0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landscape)
        contentView.data=this
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(curveEvent: CurveEvent) {
        leafObs.set(curveEvent.leaf)
        contentView.imgBack.setOnClickListener { finish() }
        initAxis(curveEvent.dryLineDataSet,
                curveEvent.wetLineDataSet,
                curveEvent.dryTempLineDataSet,
                curveEvent.wetTempLineDataSet,
                curveEvent.auxiliaryLineDataSet,
                curveEvent.wetLineDataSet2)
        EventBus.getDefault().removeStickyEvent(curveEvent)
    }

    private fun initAxis(dryLineDataSet: LineDataSet,
                         wetLineDataSet: LineDataSet,
                         dryTempLineDataSet: LineDataSet,
                         wetTempLineDataSet: LineDataSet,
                         auxiliaryLineDataSet: LineDataSet,
                         wetLineDataSet2: LineDataSet) {
        val lineData = LineData(auxiliaryLineDataSet, dryLineDataSet, wetLineDataSet, dryTempLineDataSet, wetTempLineDataSet, wetLineDataSet2)
        val xAxis = contentView.lineChart.xAxis
        val axisLeft = contentView.lineChart.axisLeft
        val axisRight = contentView.lineChart.axisRight

        xAxis.isEnabled = false     //去掉x轴
        axisLeft.isEnabled = false  //去掉左y轴
        axisRight.isEnabled = false //去掉右y轴

        lineData.setValueTextColor(ContextCompat.getColor(this, R.color.colorTextWeek))
        lineData.setValueTextSize(10f)                      //数字大小
        contentView.lineChart.legend.isEnabled = false          //去掉左下角描述
        val marker: IMarker = CustomMarkView(this, R.layout.layout_mark)
        contentView.lineChart.marker = marker
        contentView.lineChart.description = Description().apply { text = "" }//去掉右下角的描述
//        contentView.lineChart.setTouchEnabled(false)           //禁止全部手势交互
        contentView.lineChart.isDragEnabled = false
        contentView.lineChart.isScaleYEnabled = false
        contentView.lineChart.isScaleXEnabled = false
        contentView.lineChart.data = lineData                  //设置数据
        contentView.lineChart.setNoDataText("暂无数据")
        contentView.lineChart.invalidate()                     //刷新view
        showContentView()
    }

}