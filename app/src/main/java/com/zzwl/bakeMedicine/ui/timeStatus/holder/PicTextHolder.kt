package com.zzwl.bakeMedicine.ui.timeStatus.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemTobaccoPicTextBinding
import com.zzwl.bakeMedicine.room.entity.remote.HouseInfo

class PicTextHolder(val houseInfo: HouseInfo, val selectedIdObs: ObservableField<Int?>) : BaseItem<ItemTobaccoPicTextBinding>() {
    override val layoutId: Int = R.layout.item_tobacco_pic_text

}