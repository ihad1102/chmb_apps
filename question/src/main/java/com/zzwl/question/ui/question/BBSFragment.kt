package com.zzwl.question.ui.question

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.api.ErrorCode
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.ViewSlideBottom
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionFragmentBbsBinding
import com.zzwl.question.databinding.QuestionViewFilterQuestionBinding
import com.zzwl.question.event.RefreshCommentAmount
import com.zzwl.question.event.RefreshHomeQuestion
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.question.holder.BBSQuestionHolder
import com.zzwl.question.ui.question.holder.FilterHolder
import com.zzwl.question.ui.question.holder.OnlineHolder
import com.zzwl.question.viewModel.QuestionViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by G on 2017/11/13 0013.
 */
class BBSFragment : BaseFragment<QuestionFragmentBbsBinding>(), PopupWindow.OnDismissListener {

    override fun setContentView(): Int = R.layout.question_fragment_bbs
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private val questionViewModel by lazy { ViewModelProviders.of(this).get(QuestionViewModel::class.java) }
    private val filterObs by lazy { ObservableField("筛选") }

    private var listStatus = ListStatus.Content
    private var isSolved: Boolean? = null
    override var hasToolbar: Boolean = true
    override var hasStatusBar: Boolean = true
    override fun onLazyLoadOnce() {
        initView()
        obsQuestionList()
        onReload()
    }

    override fun onReload() {
        questionViewModel.operateQuestionList(1)
    }

    private fun initView() {
        toolbar.title = "论坛"
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                val limit = questionViewModel.limit
                val pageNum = if (questionViewModel.questionList.size / limit == 0) 1 else questionViewModel.questionList.size / limit + 1
                questionViewModel.operateQuestionList(pageNum, isSolved)
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }
        }

        contentView.refreshLayout.setOnRefreshListener({
            if (listStatus == ListStatus.Content) {
                questionViewModel.operateQuestionList(1, isSolved)
                listStatus = ListStatus.Refreshing
            } else {
                toast("正在执行其他操作请稍后再试")
                contentView.refreshLayout.isRefreshing = false
            }
        })

        contentView.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {}
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ViewSlideBottom.animateIn(contentView.fab)
                } else {
                    ViewSlideBottom.animateOut(contentView.fab)
                }
            }
        })
        contentView.fab.setOnClickListener({
            ARouter.getInstance().build(RouterPage.PostQuestionActivity).withInt("type", 1).navigation(activity)//发布问题页面
        })
    }

    override fun onTokenChange(data: TokenEntity?) {
    }

    private fun obsQuestionList() {
        questionViewModel.obsQuestionList()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                questionViewModel.questionList.addAll(it.data!!)
                            } else {
                                questionViewModel.questionList.clear()
                                questionViewModel.questionList.addAll(it.data!!)
                                contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.size < questionViewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            applyData()
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.LoadMore -> {
                                    if (it.error?.code == ErrorCode.EMPTY) {
                                        adapter.setLoadingNoMore()
                                    } else {
                                        adapter.setLoadingFailed()
                                    }
                                }
                                ListStatus.Content -> {
                                    showEmpty(it.error?.message)
                                }
                                ListStatus.Refreshing -> {
                                    contentView.refreshLayout.isRefreshing = false
                                }
                            }
                            listStatus = ListStatus.Content
                        }
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshQuestion(refreshEvent: RefreshHomeQuestion) {
        questionViewModel.questionList.add(0, refreshEvent.questionEntity)
        adapter.renderItems.add(1,
                BBSQuestionHolder(refreshEvent.questionEntity).apply {
                    setOnClickListener {
                        ARouter.getInstance().build(RouterPage.QuestionDetailsActivity).with(Bundle().apply {
                            putInt("id", refreshEvent.questionEntity.id ?: 0)
                        }).navigation(activity)
                    }
                }
        )

        adapter.notifyDataSetChanged()
    }


    private fun applyData() {
        adapter.diffItems.add(OnlineHolder(filterObs).apply {
            setOnClickListener {
                openPopupWindow(it)
            }
        })

        adapter.diffItems.addAll(questionViewModel.questionList.mapIndexed { index, question ->
            BBSQuestionHolder(question!!).apply {
                setOnClickListener {

                    ARouter.getInstance().build(RouterPage.QuestionDetailsActivity).with(Bundle().apply {
                        putInt("id", question.id ?: 0)
                    }).navigation(activity)
                }
            }
        })
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    when {
                        baseItem is OnlineHolder && baseItem1 is OnlineHolder -> {
                            true
                        }
                        baseItem is BBSQuestionHolder && baseItem1 is BBSQuestionHolder -> {
                            baseItem.questionEntity.id == baseItem1.questionEntity.id
                        }
                        else -> false
                    }
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    when {
                        baseItem is OnlineHolder && baseItem1 is OnlineHolder -> {
                            true
                        }
                        baseItem is BBSQuestionHolder && baseItem1 is BBSQuestionHolder -> {
                            baseItem.questionEntity.id == baseItem1.questionEntity.id
                                    && baseItem.questionEntity.replyCount == baseItem1.questionEntity.replyCount
                                    && baseItem.questionEntity.location == baseItem1.questionEntity.location
                        }
                        else -> false
                    }
                }
        )
    }


    private fun openPopupWindow(view: View) {
        //设置PopupWindow
        backgroundAlpha(0.9f)
        val inflateDataBinding = DataBindingUtil.inflate<QuestionViewFilterQuestionBinding>(LayoutInflater.from(context), R.layout.question_view_filter_question, null, false)
        val adapter = setupRecyclerView(inflateDataBinding.recyclerView)
        val array = arrayOf("全部", "已解决", "未解决")
        val popupWindow = PopupWindow(inflateDataBinding.root, 60.dp(), FrameLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(view, 0, 16, Gravity.CENTER)
        popupWindow.setOnDismissListener(this@BBSFragment)
        adapter.renderItems.addAll(array.map {
            FilterHolder(it).apply {
                setOnClickListener {
                    when (item) {
                        "全部" -> {
                            isSolved = null
                            filterObs.set("全部")
                        }
                        "已解决" -> {
                            filterObs.set("已解决")
                            isSolved = true
                        }
                        "未解决" -> {
                            filterObs.set("未解决")
                            isSolved = false
                        }
                    }
                    questionViewModel.operateQuestionList(1, isSolved)
                    popupWindow.dismiss()
                }
            }
        })
        adapter.notifyDataSetChanged()
    }

    override fun onDismiss() {
        backgroundAlpha(1f)
    }

    private fun backgroundAlpha(bgAlpha: Float) {
        val layoutParams = activity!!.window.attributes
        layoutParams.alpha = bgAlpha
        activity!!.window.attributes = layoutParams
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddCommentCount(refreshCommentAmount: RefreshCommentAmount) {
        val bbsQuestionHolder = adapter.renderItems.first { (it is BBSQuestionHolder) && it.questionEntity.id == refreshCommentAmount.id } as BBSQuestionHolder
        val count = bbsQuestionHolder.commentCountObs.get()!!
        bbsQuestionHolder.commentCountObs.set(count + 1)
//        adapter.notifyItemChanged(index)
    }
}
