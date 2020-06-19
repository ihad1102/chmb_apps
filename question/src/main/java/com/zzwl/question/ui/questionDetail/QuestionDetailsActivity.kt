package com.zzwl.question.ui.questionDetail

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.api.QuestionApi
import com.zzwl.question.databinding.QuestionActivityQuestionDetailBinding
import com.zzwl.question.event.RefreshQuestionDetailEvent
import com.zzwl.question.other.extend.progressOauthSubscribe
import com.zzwl.question.room.entity.common.QuestionDetailEntity
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.questionDetail.holder.CommentHolder
import com.zzwl.question.ui.questionDetail.holder.QuestionDetailHolder
import com.zzwl.question.viewModel.QuestionDetailViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Created by qhn on 2018/1/5.
 */
@Route(path = RouterPage.QuestionDetailsActivity)
class QuestionDetailsActivity : BaseActivity<QuestionActivityQuestionDetailBinding>(), PopupWindow.OnDismissListener {
    val viewModel by lazy { ViewModelProviders.of(this).get(QuestionDetailViewModel::class.java) }
    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private lateinit var isAdoptObs: ObservableField<Boolean>
    private var popupWindow: PopupWindow? = null
    private var popupWindowGrade: PopupWindow? = null
    private lateinit var questionDetailHolder: QuestionDetailHolder
    private var selectedCommentId: Int? = 0//点击的评论id
    override var hasToolbar: Boolean = true
    private var listStatus = ListStatus.Content
    private var replierVerified: Int = 0
    private var replierId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_activity_question_detail)
        initView()
        obsQuestionDetail()
        onReload()
    }

    override fun onReload() {
        val temp = if (TokenManage.isOauth()) TokenManage.getToken()?.userId else null
        viewModel.operateQuestionDetailObs(temp, intent.getIntExtra("id", -1))
    }

    private fun initView() {
        toolbar.title = "问题详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.refreshLayout.setOnRefreshListener({
            if (listStatus == ListStatus.Content) {
                onReload()
                listStatus = ListStatus.Refreshing
            } else {
                toast("正在执行其他操作,请稍后再试")
                contentView.refreshLayout.isRefreshing = false
            }

        })
        contentView.btnSolution.setOnClickListener({
            ARouter.getInstance().build(RouterPage.PostQuestionActivity)
                    .withInt("type", 0)
                    .withInt("id", intent.getIntExtra("id", 0))
                    .navigation(this)
        })
    }


    private fun bindRecyclerView(questionDetails: QuestionDetailEntity) {
        val linearLayoutHelper = LinearLayoutHelper()
        val linearLayoutHelper2 = LinearLayoutHelper()
        val helperList = ArrayList<LayoutHelper>()
        linearLayoutHelper.itemCount = 1
        linearLayoutHelper2.itemCount = questionDetails.replys?.size ?: 0
        linearLayoutHelper2.setMargin(0, 12.dp(), 0, 0)
        helperList.add(linearLayoutHelper)
        helperList.add(linearLayoutHelper2)
        adapter.layoutHelpers = helperList
        questionDetailHolder = QuestionDetailHolder(
                questionDetails.adoptReplyId,
                questionDetails.subImages,
                questionDetails.imageHost,
                questionDetails.postUser?.headImg?.url,
                questionDetails.postUser?.userName,
                questionDetails.postTime,
                questionDetails.content
        )
        adapter.diffItems.add(questionDetailHolder)
        questionDetails.replys?.forEach { reply ->
            adapter.diffItems.add(CommentHolder(questionDetails.imageHost, reply).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.imgOppose -> {
                            approveOrOppose(comment?.id, false, it)
                        }
                        R.id.imgApproval -> {
                            approveOrOppose(comment?.id, true, it)
                        }
                        R.id.constraintLayout -> {
                            selectedCommentId = comment?.id
                            isAdoptObs = obsIsAdopt
                            replierId = comment?.replyUser?.userId ?: 0
                            replierVerified = comment?.replyUser?.verified ?: 0
                            openPopupWindow()
                        }
                        R.id.tvReply3 -> {
                            ARouter.getInstance().build(RouterPage.ReplyListActivity)
                                    .withObject("reply", reply)
                                    .withString("hostUrl", questionDetails.imageHost)
                                    .navigation(this@QuestionDetailsActivity)
                        }
                    }

                }
            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { oldItem: BaseItem<*>, oldIndex: Int, newItem: BaseItem<*>, newIndex: Int ->
                    when {
                        oldItem is QuestionDetailHolder && newItem is QuestionDetailHolder -> {
                            oldItem.tvAdopt.get() == newItem.tvAdopt.get()
                        }
                        oldItem is CommentHolder && newItem is CommentHolder -> {
                            oldItem.comment?.id == newItem.comment?.id
                        }
                        else -> false
                    }
                }, { oldItem: BaseItem<*>, oldIndex: Int, newItem: BaseItem<*>, newIndex: Int ->
            when {
                oldItem is QuestionDetailHolder && newItem is QuestionDetailHolder -> {
                    true
                }
                oldItem is CommentHolder && newItem is CommentHolder -> {
                    oldItem.comment?.adopt == newItem.comment?.adopt &&
                            oldItem.comment?.comments?.size == newItem.comment?.comments?.size &&
                            oldItem.comment?.like == newItem.comment?.like
                }
                else -> false
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun approveOrOppose(commentId: Int?, isApprove: Boolean, view: View) {
        val type = if (isApprove) 1 else 0
        apiProvider.create(QuestionApi::class.java)
                .approveOrOppose(commentId, type)
                .throttleFirst(1L, TimeUnit.SECONDS)
                .mainIoSchedulers()
                .progressOauthSubscribe(
                        onNext = {
                            if (isApprove) {
                                (view as ImageView).setImageResource(R.drawable.question_zan_selected)
                            } else {
                                (view as ImageView).setImageResource(R.drawable.question_fandui_selected)
                            }
                        })
    }

    private fun obsQuestionDetail() {
        viewModel.getQuestionDetail().resultNotNull().observeEx(this, {
            when (it.status) {
                Status.Loading -> {
                    showLoading()
                }
                Status.Content -> {
                    if (contentView.refreshLayout.isRefreshing)
                        contentView.refreshLayout.isRefreshing = false
                    bindRecyclerView(it.data!!)
                    showContentView()
                    if (it.data!!.replys?.size == 0) {
                        contentView.imgErr.visibility = View.VISIBLE
                        contentView.textErr.visibility = View.VISIBLE
                    } else {
                        contentView.imgErr.visibility = View.GONE
                        contentView.textErr.visibility = View.GONE
                    }
                    listStatus = ListStatus.Content
                }
                Status.Error -> {
                    if (it.error?.code == ErrorCode.EMPTY) {
                        showEmpty()
                    } else {
                        showError(it.error?.message)
                        listStatus = ListStatus.Content
                    }
                }
            }
        })
    }


    private fun openPopupWindow() {
        //设置PopupWindow
        val view1: View = LayoutInflater.from(this).inflate(R.layout.question_item_popup_adopt, null)
        popupWindow = PopupWindow(view1, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        popupWindow!!.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isFocusable = true
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.animationStyle = R.style.PopupWindow
        popupWindow!!.showAtLocation(view1, Gravity.BOTTOM, 0, 0)
        popupWindow!!.setOnDismissListener(this)
        contentView.viewShadow.visibility = View.VISIBLE
    }

    fun onPopupViewClick(view: View) {
        when (view.id) {
            R.id.tvAdopt -> {
//                adoptComment()
//                ARouter.getInstance().build(RouterPage.MyselfActivity).withInt("ActivityTag", 2).navigation(this@QuestionDetailsActivity)TODO
                popupWindow!!.dismiss()
            }
            R.id.tvReply -> {
                if (selectedCommentId != null)
                    ARouter.getInstance().build(RouterPage.ReplyActivity).withInt("selectedCommentId", selectedCommentId!!).navigation(this)
                popupWindow!!.dismiss()
            }
            R.id.tvCancel -> {
                popupWindow!!.dismiss()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshEvent: RefreshQuestionDetailEvent) {
        if (refreshEvent.isRefresh) {
            onReload()
        }
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data != null)
            onReload()
    }

    override fun onDismiss() {
        if (popupWindowGrade != null)
            popupWindowGrade!!.dismiss()
        if (popupWindow != null)
            popupWindow!!.dismiss()
        contentView.viewShadow.visibility = View.GONE
    }
}