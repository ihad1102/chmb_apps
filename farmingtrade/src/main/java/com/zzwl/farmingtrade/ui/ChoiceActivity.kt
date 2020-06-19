package com.zzwl.farmingtrade.ui

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivityChoiceBinding
import com.zzwl.farmingtrade.router.RouterPage


/**
 * userType:0,1 :卖货,买货(农民,采购商)
 * comefrom 0,1 : 从首页进入,从切换身份进入,
 */
@Route(path = RouterPage.ChoiceActivity, extras = RouteExtras.NeedOauth)
class ChoiceActivity : BaseActivity<FarmingActivityChoiceBinding>() {

    override var hasToolbar: Boolean = true
    val checkedObs by lazy { ObservableField(true) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_choice)
        contentView.data = this
        initView()
        showContentView()

    }

    @SuppressLint("CommitPrefEdits")
    private fun initView() {
        toolbar.title = "目的选择"
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        contentView.imgBuy.setOnClickListener {
            //采购商
            setUserType(1)
            ARouter.getInstance().build(RouterPage.CompleteInfoActivity).navigation(this)
        }
        contentView.imgSell.setOnClickListener {
            //农民
            setUserType(0)
            ARouter.getInstance().build(RouterPage.PurchaseHallActivity).navigation(this)
        }
        contentView.imgConfirm.setOnClickListener {

            if (checkedObs.get()!!) {
                checkedObs.set(false)
            } else {
                checkedObs.set(true)
            }
        }

    }

    private fun setUserType(userType: Int) {
        if (checkedObs.get()!!)
            getSharedPreferences("userType", Context.MODE_PRIVATE).edit().putInt("userType", userType).apply()
    }
}
