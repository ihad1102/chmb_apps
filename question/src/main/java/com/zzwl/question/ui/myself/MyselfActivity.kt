package com.zzwl.question.ui.myself

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionActivityMyselfBinding
import com.zzwl.question.router.RouterPage

/**
 * Created by qhn on 2018/1/9.
 * 我的问题:0, 我的回答:1,
 */
@Route(path = RouterPage.MyselfActivity, extras = RouteExtras.NeedOauth)
class MyselfActivity : BaseActivity<QuestionActivityMyselfBinding>() {
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_activity_myself)
        showContentView()
        initView()
    }

    private fun initView() {
        toolbar.title = when (intent.getIntExtra("type", 0)) {
            0 -> "我的问题"
            1 -> "我的回答"
            else -> ""
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val userId = TokenManage.getToken()!!.userId
        val transaction = supportFragmentManager.beginTransaction()
        val myselfFragment = ARouter.getInstance()
                .build(RouterPage.MyselfFragment)
                .withInt("type", intent.getIntExtra("type", 0))
                .withInt("userId", userId.toInt())
                .navigation(this) as MyselfFragment
        transaction.replace(R.id.flMyself, myselfFragment)
        transaction.commit()
    }


}