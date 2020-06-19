package com.zzwl.bakeMedicine.event

import com.github.mikephil.charting.data.LineDataSet

data class CurveEvent(val dryLineDataSet: LineDataSet,
                      val wetLineDataSet: LineDataSet,
                      val dryTempLineDataSet: LineDataSet,
                      val wetTempLineDataSet: LineDataSet,
                      val auxiliaryLineDataSet: LineDataSet,
                      val wetLineDataSet2: LineDataSet,
                      val leaf: Int,
                      val isKeepingTmp:Boolean)