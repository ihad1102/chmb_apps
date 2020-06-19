package com.zzwl.information.ui

import android.os.Bundle
import com.g.base.ui.BaseFragment
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoViewSmartTabViewpagerBinding

class WikiFragment : BaseFragment<InfoViewSmartTabViewpagerBinding>() {
    override fun setContentView(): Int = R.layout.info_view_smart_tab_viewpager
    override fun onLazyLoadOnce() {
        initView()
        showContentView()
    }

    private fun initView() {
        val adapter = FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(activity!!)
                .add("病害", WikiSubFragment::class.java, Bundle().apply { putInt("type", 1) })
                .add("虫害", WikiSubFragment::class.java, Bundle().apply { putInt("type", 2) })
                .create())
        contentView.viewPagerLayout.viewPager.offscreenPageLimit = 2
        contentView.viewPagerLayout.viewPager.adapter = adapter
        contentView.smartTabLayout.smartTabLayout.setViewPager(contentView.viewPagerLayout.viewPager)
    }

}