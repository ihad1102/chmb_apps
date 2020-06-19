package com.zzwl.bakeMedicine.ui.timeStatus

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

class TempValueFormatter : IValueFormatter {
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        return if (entry?.data != null) {
             (entry.data as TextData).value
        } else
            ""
    }
}