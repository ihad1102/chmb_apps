package com.zzwl.bakeMedicine.ui.timeStatus.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTobaccoContentBinding

class TobaccoContentHolder(
        val combustionOutputStatusObs: ObservableField<Boolean>,
        val arefactionStatusObs: ObservableField<Boolean>,
        val fanObs: ObservableField<String>,
        val fanStatusObs: ObservableField<Boolean>,
        val iconKeyObs: ObservableField<Boolean>,
        val iconTrumpetObs: ObservableField<Boolean>,
        val iconStandbyCellObs: ObservableField<Boolean>,
        val iconFrequencyObs: ObservableField<Boolean>,
        val iconNetworkObs: ObservableField<Boolean>,
        val isPartialTemperatureObs: ObservableField<Boolean>,
        val isMalfunctionObs: ObservableField<Boolean>,
        val dryBulbTempObs: ObservableField<Float>,
        val dryBulbGoalTempObs: ObservableField<Float>,
        val wetBulbTempObs: ObservableField<Float>,
        val wetBulbGoalTempObs: ObservableField<Float>,
        val temperatureStatusObs: ObservableField<Boolean>,
        val RHObs: ObservableField<String>,
        val otherDryTemp: ObservableField<Float>,
        val otherWestTemp: ObservableField<Float>,
        val workStepTimeObs: ObservableField<Float>,
        val workTotalTimeObs: ObservableField<Float>,
        val powerVoltageObs: ObservableField<Int>,
        val stepObs: ObservableField<Int>,
        val controlShedObs: ObservableField<Boolean>) : BaseItem<ItemTobaccoContentBinding>() {


    override val layoutId: Int = R.layout.item_tobacco_content

}