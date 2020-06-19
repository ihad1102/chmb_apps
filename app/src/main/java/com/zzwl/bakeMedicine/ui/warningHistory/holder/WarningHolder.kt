package com.zzwl.bakeMedicine.ui.warningHistory.holder

import android.support.v4.content.ContextCompat
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemWarningBinding

class WarningHolder(
        val houseId: Int?,
        val isTitle: Boolean,
        val isCurrentWarning: Boolean,
        val warningName: String,
        val level: String,
        val tobaccoName: String,
        val startTime: String,
        val resumeTime: String,
        val isGray: Boolean) : BaseItem<ItemWarningBinding>() {
    override val layoutId: Int = R.layout.item_warning
    override fun onBind(binding: ItemWarningBinding) {
        when (level) {
            "提醒" -> binding.tvLevel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorCyanWeek))
            "一般" -> binding.tvLevel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorYellow))
            "重要" -> binding.tvLevel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorYellowDark))
            "严重" -> binding.tvLevel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorRedDark))
            "级别" -> binding.tvLevel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorGreenDark))
        }
    }
}