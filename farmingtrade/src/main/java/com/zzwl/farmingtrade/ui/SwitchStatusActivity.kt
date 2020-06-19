package com.zzwl.farmingtrade.ui

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.router.BasePage
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivitySwitchBinding
import com.zzwl.farmingtrade.router.RouterPage


/**
 * userType:0,1 :卖货,买货(农民,采购商)
 */
@Route(path = RouterPage.SwitchStatusActivity, extras = RouteExtras.NeedOauth)
class SwitchStatusActivity : BaseActivity<FarmingActivitySwitchBinding>() {
    @Autowired
    @JvmField
    var comefrom = 0
    override var hasToolbar: Boolean = true

    val currentStatusObs by lazy { ObservableField("您当前的身份为\"收货方\"") }
    val switchStatusObs by lazy { ObservableField("切换为\"卖货方\"身份") }
    val imgObs by lazy { ObservableField(R.drawable.farming_ic_supply) }
    var userType = 1
    var isFirst = true//第一次进入
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_switch)
        contentView.data = this
        userType = getSharedPreferences("userType", Context.MODE_PRIVATE).getInt("userType", 1)
        initView()
        showContentView()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initView() {
        toolbar.title = "身份切换"
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            ARouter.getInstance().build(BasePage.Home).navigation(this)
        }
        switchStatus()
        contentView.tvSwitchStatus.setOnClickListener {
            userType = if (userType == 0) 1 else 0
            getSharedPreferences("userType", Context.MODE_PRIVATE).edit().putInt("userType", userType).apply()
            switchStatus()
        }
        contentView.tvBack.setOnClickListener {
            ARouter.getInstance().build(BasePage.Home).navigation(this)
        }

    }

    private fun switchStatus() {
        when (userType) {
            0 -> {
                currentStatusObs.set("您当前的身份为\"卖货方\"")
                switchStatusObs.set("切换为\"收货方\"身份")
                imgObs.set(R.drawable.farming_ic_supply)
                if (!isFirst)
                    ARouter.getInstance().build(RouterPage.PurchaseHallActivity).navigation(this)

            }
            1 -> {
                currentStatusObs.set("您当前的身份为\"收货方\"")
                switchStatusObs.set("切换为\"卖货方\"身份")
                imgObs.set(R.drawable.farming_ic_purchase_status)
                if (!isFirst)
                    ARouter.getInstance().build(RouterPage.CompleteInfoActivity).navigation(this)

            }
        }
        isFirst = false
    }


}
