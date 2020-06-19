package com.zzwl.farmingtrade.ui.myTrading

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingViewSmartTabViewpagerBinding
import com.zzwl.farmingtrade.router.RouterPage

/**
 * Created by qhn on 2017/12/1.
 */
@Route(path = RouterPage.MyTradingActivity, extras = RouteExtras.NeedOauth)
class MyTradingActivity : BaseActivity<FarmingViewSmartTabViewpagerBinding>() {
    override var hasToolbar: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_view_smart_tab_viewpager)
        initView()
        showContentView()
    }

    private fun initView() {
        //toolbar
        toolbar.title = "我的买卖"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add("我的供应", MyTradingFragment::class.java, Bundle().apply { putInt("type", 1) })
                .add("我的采购", MyTradingFragment::class.java, Bundle().apply { putInt("type", 2) })
                .add("我的交易", MyTradingFragment::class.java, Bundle().apply { putInt("type", 3) })
                .create())
        contentView.viewPagerLayout.viewPager.offscreenPageLimit = 3
        contentView.viewPagerLayout.viewPager.adapter = adapter
        contentView.smartTabLayout.smartTabLayout.setViewPager(contentView.viewPagerLayout.viewPager)
        contentView.viewPagerLayout.viewPager.currentItem = intent.getIntExtra("type", 0)
    }


}