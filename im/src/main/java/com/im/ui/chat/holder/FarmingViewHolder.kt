package com.im.ui.chat.holder

import android.databinding.DataBindingUtil
import android.view.View
import cn.jiguang.imui.messages.BaseMessageViewHolder
import cn.jiguang.imui.messages.MessageListStyle
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jpush.im.android.api.content.CustomContent
import com.alibaba.android.arouter.launcher.ARouter
import com.im.databinding.ItemFarmingBinding
import com.im.router.AppRoute
import com.im.ui.chat.models.CustomMessageConst
import com.im.ui.chat.models.FarmingViewBind
import com.im.ui.chat.models.MyMessage


class FarmingViewHolder(val view: View,val isSend : Boolean) : BaseMessageViewHolder<MyMessage>(view), MsgListAdapter.DefaultMessageViewHolder {
    override fun onBind(data: MyMessage?) {
        val customContent = data?.message?.content as? CustomContent ?: return
        val bind = DataBindingUtil.bind<ItemFarmingBinding>(view) ?: return
        //设置值
        val price = customContent.getStringValue(FarmingViewConst.Price) ?: ""
        val crop = customContent.getStringValue(FarmingViewConst.Crop) ?: ""
        val image = customContent.getStringValue(FarmingViewConst.Image) ?: ""
        bind.data = FarmingViewBind(price, crop, image)
        bind.executePendingBindings()
        //跳转
        view.setOnClickListener {
            val id = customContent.getStringValue(CustomMessageConst.Id)
            ARouter.getInstance().build(AppRoute.SupplierDetailsActivity)
                    .withInt("id", id.toInt()).navigation(view.context)
        }
    }

    override fun applyStyle(style: MessageListStyle?) {

    }

}

object FarmingViewConst {
    const val Crop = "Crop"
    const val Price = "Amount"
    const val Image = "Image"
}