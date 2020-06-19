package com.zzwl.farmingtrade.ui.crops.holder


import android.databinding.ObservableField
import android.view.View
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemRcyCropBinding
import com.zzwl.farmingtrade.room.entity.remote.FollowedCropEntity


class CropHolder(val cropEntity: FollowedCropEntity?, val isFollowed: Boolean = false, val isSelectedId: Int? = null) : BaseItem<FarmingItemRcyCropBinding>() {
    override val layoutId: Int = R.layout.farming_item_rcy_crop
    val imgSelect = ObservableField<Boolean>()
    override fun onBind(binding: FarmingItemRcyCropBinding) {
        imgSelect.set(if (isFollowed) cropEntity?.check else false)
        if (isSelectedId == cropEntity?.id) binding.imgSelect.visibility = View.VISIBLE
    }

}