package com.zzwl.farmingtrade.event

/**
 * Created by qhn on 2018/1/15.
 * type, 0:删除,1:添加
 */
data class SelectedCropsEvent(val cropId:Int?, val type:Int,val cropName:String)