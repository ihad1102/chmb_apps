package com.zzwl.bakeMedicine.ui.timeStatus

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.zzwl.bakeMedicine.R

@SuppressLint("ViewConstructor")
class CustomMarkView(context: Context, layoutResult: Int) : MarkerView(context, layoutResult) {

    var tvContent: TextView = findViewById(R.id.tvContent)
    var relativeLayout: RelativeLayout = findViewById(R.id.relativeLayout)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        var isShowMarkView = true
        val text = if (e?.data != null) {
            isShowMarkView = (e.data as TextData).describe.isNotEmpty()
            (e.data as TextData).describe + (e.data as TextData).value + (e.data as TextData).unit
        } else
            ""
        if (text.isEmpty() || !isShowMarkView) {
            relativeLayout.visibility = View.GONE
        } else {
            relativeLayout.visibility = View.VISIBLE
            tvContent.text = text
        }

        super.refreshContent(e, highlight)

    }

    private var mOffset: MPPointF? = null
    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return super.getOffset()
    }
}