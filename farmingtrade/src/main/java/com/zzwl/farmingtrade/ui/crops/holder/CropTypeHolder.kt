package com.zzwl.farmingtrade.ui.crops.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemRcyCropTypeBinding
import com.zzwl.farmingtrade.room.entity.common.CropTypeEntity

/**
 * Created by qhn on 2018/1/8.
 */
class CropTypeHolder(val cropTypeEntity: CropTypeEntity, val position: Int, val observableField: ObservableField<Int>) : BaseItem<FarmingItemRcyCropTypeBinding>() {
    override val layoutId: Int = R.layout.farming_item_rcy_crop_type
}