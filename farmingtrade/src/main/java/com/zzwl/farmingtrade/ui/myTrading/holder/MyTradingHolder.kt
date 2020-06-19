package com.zzwl.farmingtrade.ui.myTrading.holder

import android.databinding.ObservableField
import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingItemMyTradingBinding
import com.zzwl.farmingtrade.room.entity.remote.MyTradeItem
import java.math.BigDecimal

class MyTradingHolder(val myTradeItem: MyTradeItem,
                      val id: String,
                      val statusTempObs: ObservableField<String>,
                      val btnTempObs: ObservableField<String>,
                      val userType: Int,
                      val isShowBtn2Obs: ObservableField<Boolean>) : BaseItem<FarmingItemMyTradingBinding>() {
    override val layoutId: Int = R.layout.farming_item_my_trading
    val subsidyObs by lazy {
        ObservableField("(交易金额: ¥${BigDecimal(myTradeItem.totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()}" +
                " 补贴金额: ¥${BigDecimal(myTradeItem.totalSubsidy).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()})")
    }
    val realPayMoneyObs by lazy { ObservableField(BigDecimal(myTradeItem.totalPrice - myTradeItem.totalSubsidy).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()) }
}