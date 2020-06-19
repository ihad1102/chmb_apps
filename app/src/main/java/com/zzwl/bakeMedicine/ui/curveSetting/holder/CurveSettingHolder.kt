package com.zzwl.bakeMedicine.ui.curveSetting.holder

import android.databinding.ObservableField
import android.view.View
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemCurveTypeBinding

class CurveSettingHolder(val title:String,
                         val title2:String,
                         val dryBulbTempObs:ObservableField<String>,
                         val dryBulbTemp2Obs:ObservableField<String>,
                         val wetBulbTempObs:ObservableField<String>,
                         val wetBulbTemp2Obs:ObservableField<String>,
                         val heatingTimeObs:ObservableField<String>,
                         val heatingTime2Obs:ObservableField<String>,
                         val keepingTimeObs:ObservableField<String>,
                         val keepingTime2Obs:ObservableField<String>) : BaseItem<ItemCurveTypeBinding>() {
    override val layoutId: Int = R.layout.item_curve_setting


}