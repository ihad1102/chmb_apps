package com.zzwl.farmingtrade.ui.supplier.holder

import android.graphics.drawable.Drawable
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.g.base.extend.getPicHeightOrWidth
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemQuestionPicBinding

/**
 * Created by qhn on 2018/1/2.
 * spacing: 图片与图片之间,图片与屏幕之间的间隔的和,默认为问答首页问题holder的16+16+6+6
 */
class PicHolder(var imgUrl: Any?,
                val showCancelImg: Boolean = false,
                val drawable: Drawable? = null,
                val clickable: Boolean = false,
                val isPostPic: Boolean = false,
                val spacing: Int = 44) : BaseItem<FarmingItemQuestionPicBinding>() {
    override val layoutId: Int = R.layout.farming_item_question_pic
    var image: Any? = null
    private var params: VirtualLayoutManager.LayoutParams? = null

    override fun onBind(binding: FarmingItemQuestionPicBinding) {
        imgUrl = imgUrl ?: R.drawable.farming_add_img
        //设置图片宽高 ,没有cancelImg的 不设置
        val divisor = if (!isPostPic) 3 else 4
        params = VirtualLayoutManager.LayoutParams(
                binding.root.context.getPicHeightOrWidth(binding.root.context, divisor, spacing)
                , binding.root.context.getPicHeightOrWidth(binding.root.context, divisor, spacing))
        binding.relativeLayout.layoutParams = params
    }

}