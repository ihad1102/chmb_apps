package com.zzwl.question.ui.expert

import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.ui.BaseActivity
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionActivityMyFollowBinding
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.expert.fragment.ExpertFragment

@Route(path = RouterPage.MyFollowedActivity)
class MyFollowedActivity : BaseActivity<QuestionActivityMyFollowBinding>() {
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_activity_my_follow)
        initView()
    }

    @SuppressLint("CommitTransaction")
    private fun initView() {
        toolbar.title = "我的关注"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(R.id.frameLayout, ExpertFragment::class.java.newInstance()).commit()
        showContentView()
    }

}