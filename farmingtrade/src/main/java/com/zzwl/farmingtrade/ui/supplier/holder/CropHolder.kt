package com.zzwl.farmingtrade.ui.supplier.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemFramingCropBinding
import com.zzwl.farmingtrade.room.entity.remote.FarmingCropEntity

class CropHolder(val framingCropEntity: FarmingCropEntity) : BaseItem<FarmingItemFramingCropBinding>() {
    override val layoutId: Int = R.layout.farming_item_framing_crop
}