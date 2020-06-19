package com.zzwl.bakeMedicine.ui.timeStatus.holder

import android.support.v4.content.ContextCompat
import com.g.base.help.d
import com.g.base.ui.recyclerView.item.BaseItem
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTobaccoTailBinding
import com.zzwl.bakeMedicine.ui.timeStatus.CustomMarkView

class TobaccoTailHolder(private val dryLineDataSet: LineDataSet,
                        private val wetLineDataSet: LineDataSet,
                        private val dryTempLineDataSet: LineDataSet,
                        private val wetTempLineDataSet: LineDataSet,
                        private val auxiliaryLineDataSet: LineDataSet,
                        private val wetLineDataSet2: LineDataSet,
                        val leaf: Int) : BaseItem<ItemTobaccoTailBinding>() {
    override val layoutId: Int = R.layout.item_tobacco_tail
    override fun onBind(binding: ItemTobaccoTailBinding) {
        initAxis(binding, dryLineDataSet, wetLineDataSet, dryTempLineDataSet, wetTempLineDataSet, auxiliaryLineDataSet, wetLineDataSet2)
    }


    private fun initAxis(binding: ItemTobaccoTailBinding,
                         dryLineDataSet: LineDataSet,
                         wetLineDataSet: LineDataSet,
                         dryTempLineDataSet: LineDataSet,
                         wetTempLineDataSet: LineDataSet,
                         auxiliaryLineDataSet: LineDataSet,
                         wetLineDataSet2: LineDataSet) {
        val lineData = LineData(auxiliaryLineDataSet, dryLineDataSet, wetLineDataSet, dryTempLineDataSet, wetTempLineDataSet, wetLineDataSet2)
        val xAxis = binding.lineChart.xAxis
        val axisLeft = binding.lineChart.axisLeft
        val axisRight = binding.lineChart.axisRight

        xAxis.isEnabled = false     //去掉x轴
        axisLeft.isEnabled = false  //去掉左y轴
        axisRight.isEnabled = false //去掉右y轴

        lineData.setValueTextColor(ContextCompat.getColor(binding.root.context, R.color.colorTextWeek))
        lineData.setValueTextSize(10f)                      //数字大小
        binding.lineChart.legend.isEnabled = false          //去掉左下角描述
        val marker: IMarker = CustomMarkView(binding.root.context, R.layout.layout_mark)
        binding.lineChart.marker = marker
        binding.lineChart.description = Description().apply { text = "" }//去掉右下角的描述
//        binding.lineChart.setTouchEnabled(false)           //禁止全部手势交互
        binding.lineChart.isDragEnabled = false
        binding.lineChart.isScaleYEnabled = false
        binding.lineChart.isScaleXEnabled = false
        binding.lineChart.data = lineData                  //设置数据
        binding.lineChart.setNoDataText("暂无数据")
        binding.lineChart.invalidate()                     //刷新view

    }
}