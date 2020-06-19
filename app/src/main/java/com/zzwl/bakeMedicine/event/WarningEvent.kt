package com.zzwl.bakeMedicine.event

/**
 * type 0,1 :  当前告警,历史告警
 */
data class WarningEvent(val type: Int, val keyword: String?, val houseId: String)