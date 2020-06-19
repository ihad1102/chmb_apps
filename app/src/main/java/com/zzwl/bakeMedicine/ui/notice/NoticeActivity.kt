package com.zzwl.bakeMedicine.ui.notice

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityNoticeBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.home.fragment.NoticeFragment

@Route(path = RouterPage.NoticeActivity)
class NoticeActivity : BaseActivity<ActivityNoticeBinding>() {
    override var hasToolbar: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        initView()
        showContentView()
    }

    private fun initView() {
        toolbar.title = "公告"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, NoticeFragment())
        transaction.commit()
    }

}