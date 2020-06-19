package com.zzwl.question.ui.postQuestion

import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.common.apiProvider
import com.g.base.extend.inhibitingInput
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.toast
import com.g.base.ui.BaseActivity
import com.zzwl.question.R
import com.zzwl.question.api.QuestionApi
import com.zzwl.question.databinding.QuestionActivityReplyBinding
import com.zzwl.question.event.RefreshQuestionDetailEvent
import com.zzwl.question.other.extend.progressOauthSubscribe
import com.zzwl.question.router.RouterPage
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * Created by qhn on 2018/1/9.
 */
@Route(path = RouterPage.ReplyActivity)
class ReplyActivity : BaseActivity<QuestionActivityReplyBinding>() {
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_activity_reply)
        showContentView()
        initView()
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        toolbar.title = "发表回复"
        toolbar.inflateMenu(R.menu.confirm_text)
        toolbar.menu.getItem(0).title = "回复"
        toolbar.setOnMenuItemClickListener {
            if (contentView.tvReply.text.toString().trim().isNotEmpty()) {
                apiProvider.create(QuestionApi::class.java)
                        .reply(intent.getIntExtra("selectedCommentId", 0), contentView.tvReply.text.toString())
                        .throttleFirst(1L, TimeUnit.SECONDS)
                        .mainIoSchedulers()
                        .progressOauthSubscribe(
                                onNext = {
                                    EventBus.getDefault().post(RefreshQuestionDetailEvent(true))
                                    finish()
                                }
                        )
            } else toast("回复不能为空")
            return@setOnMenuItemClickListener true
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        inhibitingInput(contentView.tvReply)
    }
}