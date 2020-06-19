package com.im.ui.chat.holder

import android.databinding.DataBindingUtil
import android.view.View
import cn.jiguang.imui.messages.BaseMessageViewHolder
import cn.jiguang.imui.messages.MessageListStyle
import cn.jiguang.imui.messages.MsgListAdapter
import cn.jpush.im.android.api.content.CustomContent
import com.alibaba.android.arouter.launcher.ARouter
import com.im.databinding.ItemPurchaseBinding
import com.im.router.AppRoute
import com.im.ui.chat.models.CustomMessageConst
import com.im.ui.chat.models.MyMessage
import com.im.ui.chat.models.PurchaseViewBind

class PurchaseViewHolder(val view: View,val isSend : Boolean) : BaseMessageViewHolder<MyMessage>(view), MsgListAdapter.DefaultMessageViewHolder {
    override fun onBind(data: MyMessage?) {
        val customContent = data?.message?.content as? CustomContent ?: return
        val bind = DataBindingUtil.bind<ItemPurchaseBinding>(view) ?: return
        //设置值
        val crop = customContent.getStringValue(PurchaseViewConst.Crop) ?: ""
        val amount = customContent.getStringValue(PurchaseViewConst.Amount) ?: ""
        val price = customContent.getStringValue(PurchaseViewConst.Price) ?: ""
        bind.data = PurchaseViewBind(price, crop, amount)
        bind.executePendingBindings()
        //跳转
        view.setOnClickListener {
            val id = customContent.getStringValue(CustomMessageConst.Id)
            ARouter.getInstance().build(AppRoute.BuyDetailsActivity)
                    .withInt("id", id.toInt()).navigation(view.context)
        }
    }

    override fun applyStyle(style: MessageListStyle?) {
        val  style = style ?: return
    }

}

object PurchaseViewConst {
    const val Crop = "Crop"
    const val Price = "Price"
    const val Amount = "Amount"
}