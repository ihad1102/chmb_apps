package com.zzwl.bakeMedicine.ui.home

import android.content.Intent
import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.inflateDataBinding
import com.g.base.extend.toast
import com.g.base.help.d
import com.g.base.router.BasePage
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityHomeBinding
import com.zzwl.bakeMedicine.databinding.ViewTabIconAndTextBinding
import com.zzwl.bakeMedicine.model.bindView.TabItemBind
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.home.fragment.HomeFragment
import com.zzwl.bakeMedicine.ui.home.fragment.PersonCenterFragment
import com.zzwl.bakeMedicine.ui.home.fragment.ShopFragment
import com.zzwl.bakeMedicine.ui.home.fragment.WikiAndInfoFragment
import com.zzwl.question.ui.question.BBSFragment

@Route(path = RouterPage.HomeActivity)
class HomeActivity : BaseActivity<ActivityHomeBinding>() {


    override var isFullScreen: Boolean = true
    private lateinit var view: ViewTabIconAndTextBinding
    private val currentSelect by lazy { ObservableField(0) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ARouter.getInstance().inject(this)
        if (TokenManage.isOauth() && System.currentTimeMillis() - (TokenManage.getToken()?.createTime
                        ?: 0) < 1000L * 60 * 60 * 30) {

        } else {
            TokenManage.cleanToken()
            ARouter.getInstance().build(BasePage.Oauth).navigation(this)
        }
        showContentView()
        initView()
        d("testFixBugBranch")
    }

    private fun initView() {
        val fragmentPagerItemAdapter = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add("首页", HomeFragment::class.java)
                .add("资讯", WikiAndInfoFragment::class.java)
                .add("论坛", BBSFragment::class.java)
                .add("商城", ShopFragment::class.java)
                .add("个人中心", PersonCenterFragment::class.java)
                .create())
        contentView.viewPager.adapter = fragmentPagerItemAdapter
        contentView.smartTabLayout.setCustomTabView { container, position, _ ->
            val data = when (position) {
                0 -> Pair("首页", R.drawable.ic_home)
                1 -> Pair("资讯", R.drawable.ic_information)
                2 -> Pair("论坛", R.drawable.ic_question)
                3 -> Pair("商城", R.drawable.ic_shop_selected)
                else -> Pair("个人中心", R.drawable.ic_persion_center)
            }

            view = container.inflateDataBinding(R.layout.view_tab_icon_and_text)
            view.data = TabItemBind(data.first, data.second, position, currentSelect).also {
                it.setOnClickListener {
                    selectTab(position)
                }
            }
            view.root
        }
        //创建全部的view
        contentView.viewPager.offscreenPageLimit = 5
        contentView.smartTabLayout.setViewPager(contentView.viewPager)
    }

    private fun selectTab(position: Int) {
        contentView.viewPager.setCurrentItem(position, false)
        currentSelect.set(position)
        view.executePendingBindings()
    }

    private var lastBackPressTime = 0L
    override fun onBackPressed() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastBackPressTime > 2000L) {
            toast("再按一次 退出程序")
        } else {
            super.onBackPressed()
        }
        lastBackPressTime = currentTimeMillis
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}
