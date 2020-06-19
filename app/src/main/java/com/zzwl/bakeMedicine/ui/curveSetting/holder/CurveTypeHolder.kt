package com.zzwl.bakeMedicine.ui.curveSetting.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemCurveTypeBinding

class CurveTypeHolder(val typeObs:ObservableField<Int>) : BaseItem<ItemCurveTypeBinding>() {
    override val layoutId: Int = R.layout.item_curve_type

}