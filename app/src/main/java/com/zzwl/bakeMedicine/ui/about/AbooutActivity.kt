package com.zzwl.bakeMedicine.ui.about

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.help.CallPhone
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.tencent.bugly.beta.Beta
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityAboutBinding
import com.zzwl.bakeMedicine.model.bindView.SingleTextHolder
import com.zzwl.bakeMedicine.router.RouterPage


@Route(path = RouterPage.AboutActivity)
class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override var hasToolbar: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupView()
        showContentView()
    }

    private fun setupView() {
        toolbar.title = "版本信息"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = setupRecyclerView(contentView.recyclerView)
        adapter.layoutHelpers = listOf(LinearLayoutHelper(1, 3))
        adapter.renderItems.add(SingleTextHolder("检查更新").apply {
            setOnClickListener {
                Beta.checkUpgrade(true, false)
            }
        })
        adapter.renderItems.add(SingleTextHolder("使用协议").apply {
            setOnClickListener {
                ARouter.getInstance().build(RouterPage.OauthAgreementActivity).navigation(this@AboutActivity)
            }
        })
        adapter.renderItems.add(SingleTextHolder("联系客服").apply {
            setOnClickListener {
                CallPhone.callTel(this@AboutActivity, "029-88188685")
            }
        })
        adapter.notifyDataSetChanged()
    }
}