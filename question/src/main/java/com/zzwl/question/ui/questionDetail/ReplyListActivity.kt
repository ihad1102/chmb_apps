package com.zzwl.question.ui.questionDetail

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionViewRecyclerviewBinding
import com.zzwl.question.room.entity.common.Reply
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.questionDetail.holder.ReplyHolder


@Route(path = RouterPage.ReplyListActivity, extras = RouteExtras.NeedOauth)
class ReplyListActivity : BaseActivity<QuestionViewRecyclerviewBinding>() {

    @Autowired
    @JvmField
    var reply: Reply? = null

    @Autowired
    @JvmField
    var hostUrl: String = ""
    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.question_view_recyclerview)
        initView()
        showContentView()
    }

    private fun initView() {
        toolbar.title = "回复列表"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val linearLayoutHelper = LinearLayoutHelper()
        val linearLayoutHelper2 = LinearLayoutHelper()
        val helperList = ArrayList<LayoutHelper>()
        linearLayoutHelper.itemCount = 1
        linearLayoutHelper2.itemCount = reply?.comments?.size ?: 0
        linearLayoutHelper.setMargin(0, 0, 0, 12.dp())
        helperList.add(linearLayoutHelper)
        adapter.layoutHelpers = helperList
        adapter.renderItems.add(ReplyHolder(
                reply?.replyUser?.headImg?.url,
                reply?.replyUser?.userName,
                reply?.content,
                reply?.replyTime,
                reply?.del ?: false
        ))
        reply?.comments?.forEach {
            adapter.renderItems.add(ReplyHolder(
                    it?.user?.headImg,
                    it?.user?.realName,
                    it?.content,
                    it?.commentTime,
                    it?.del ?: false
            ))
        }

        adapter.notifyDataSetChanged()
    }

}