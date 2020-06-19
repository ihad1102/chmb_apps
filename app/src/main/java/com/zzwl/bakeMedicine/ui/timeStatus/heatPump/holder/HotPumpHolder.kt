package com.zzwl.bakeMedicine.ui.timeStatus.heatPump.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemHotPumpBinding

class HotPumpHolder(
        val machineStatusObs: ObservableField<String>,
        val runningStatusObs: ObservableField<String>,
        val currentFormulaObs: ObservableField<String>,
        val currentNumberObs: ObservableField<String>,
        val currentRunningTimeObs: ObservableField<String>,
        val totalTimeObs: ObservableField<String>,
        val remainingTimeObs: ObservableField<String>,
        val currentTempObs: ObservableField<String>,
        val setTempObs: ObservableField<String>,
        val currentWetObs: ObservableField<String>,
        val setWetObs: ObservableField<String>
//        ,
//        val auxiliaryHeatingObs:ObservableField<String>,
//        val electricalHeatingObs:ObservableField<String>,
//        val auxiliaryHeating2Obs:ObservableField<String>
) : BaseItem<ItemHotPumpBinding>() {
    override val layoutId: Int = R.layout.item_hot_pump
}