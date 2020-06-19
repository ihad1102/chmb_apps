package com.zzwl.bakeMedicine.ui.home.fragment

import android.support.v7.widget.Toolbar
import android.view.Gravity
import com.g.base.extend.inflateDataBinding
import com.g.base.ui.BaseFragment
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewToolbarSmartTabBinding
import com.zzwl.bakeMedicine.databinding.ViewViewpagerBinding
import com.zzwl.information.ui.InformationFragment
import com.zzwl.information.ui.WikiFragment

class WikiAndInfoFragment : BaseFragment<ViewViewpagerBinding>() {
    override var hasToolbar: Boolean=true
    override var hasStatusBar: Boolean = true
    override fun setContentView(): Int = R.layout.view_viewpager
    private val smartTabLayout by lazy { toolbar.inflateDataBinding<ViewToolbarSmartTabBinding>(R.layout.view_toolbar_smart_tab).smartTabLayout }
    override fun onLazyLoadOnce() {
        initView()
        showContentView()
    }

    private fun initView() {
        val fragmentPagerItemAdapter = FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(activity)
                .add("资讯", InformationFragment::class.java)
                .add("百科", WikiFragment::class.java)
                .create())

        contentView.viewPager.adapter = fragmentPagerItemAdapter
        smartTabLayout.setViewPager(contentView.viewPager)

        val layoutParams = smartTabLayout.layoutParams as Toolbar.LayoutParams
        layoutParams.width = Toolbar.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        smartTabLayout.layoutParams = layoutParams
        smartTabLayout.background = null
        toolbar.addView(smartTabLayout)

    }

}