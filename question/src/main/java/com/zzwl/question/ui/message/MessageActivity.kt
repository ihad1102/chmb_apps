package com.zzwl.question.ui.message

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.help.postSimpleNotify
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.question.R
import com.zzwl.question.api.NotifyURl
import com.zzwl.question.databinding.QuestionViewSmartTabViewpagerBinding
import com.zzwl.question.router.RouterPage

/**
 * Created by G on 2017/11/25 0025.
 */
@Route(path = RouterPage.MessageActivity, extras = RouteExtras.NeedOauth)
class MessageActivity : BaseActivity<QuestionViewSmartTabViewpagerBinding>() {
    override var hasToolbar: Boolean = true

    private val smartTab by lazy { contentView.smartTabLayout.smartTabLayout }
    private val viewpager by lazy { contentView.viewPagerLayout.viewPager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_view_smart_tab_viewpager)

        setupView()
    }

    private fun setupView() {
        toolbar.title = "我的消息"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }

        viewpager.offscreenPageLimit = 3

        viewpager.adapter = FragmentPagerItemAdapter(supportFragmentManager,
                FragmentPagerItems.with(this).apply {
                    add(FragmentPagerItem.of("系统", MessageListFragment::class.java, Bundle().apply { putString("type", "1") }))
                    add(FragmentPagerItem.of("评论", MessageListFragment::class.java, Bundle().apply { putString("type", "2") }))
                    add(FragmentPagerItem.of("@我的", MessageListFragment::class.java, Bundle().apply { putString("type", "3") }))
                }.create())
        smartTab.setViewPager(viewpager)
        viewpager.currentItem = 1
        showContentView()
    }

    override fun onDestroy() {
        super.onDestroy()
        postSimpleNotify(NotifyURl.MessageCount)
    }
}