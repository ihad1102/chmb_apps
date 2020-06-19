package com.zzwl.bakeMedicine.ui.timeStatus

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.lang.StringBuilder

class TimeValueFormatter(private val xAxisValueMap: HashMap<Float, String>) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val date = xAxisValueMap[value]?.substring(8) ?: ""
        return if (date.length > 3) StringBuilder(date).insert(2, "日").toString() else date

    }

}