package com.zzwl.question.ui.myself

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.token.TokenManage
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionViewSwiperefreshRecyclerviewBinding
import com.zzwl.question.room.entity.remote.MyAnswerREntity
import com.zzwl.question.room.entity.remote.QuestionEntity
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.myself.MyQuestionHolder
import com.zzwl.question.ui.myself.MyselfActivity
import com.zzwl.question.ui.myself.holder.MyAnswerHolder
import com.zzwl.question.viewModel.MyselfViewModel

/**
 * 回复列表 暂时不用. userId 为0则请求我的回答列表否则为专家的回答列表
 */
@Route(path = RouterPage.MyselfFragment)
class MyselfFragment : BaseFragment<QuestionViewSwiperefreshRecyclerviewBinding>() {
    override fun setContentView(): Int = R.layout.question_view_swiperefresh_recyclerview
    lateinit var viewModel: MyselfViewModel
    private var listStatus = ListStatus.Content
    private val TYPE_MYQUESTION = 0
    private val TYPE_MYANSWER = 1
    val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    @JvmField
    @Autowired
    var type = 0
    @JvmField
    @Autowired
    var userId: Int? = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ARouter.getInstance().inject(this)
        viewModel = ViewModelProviders.of(this).get(MyselfViewModel::class.java)
        initView()
        when (type) {
            TYPE_MYQUESTION -> obsMyQuestion()
            TYPE_MYANSWER -> obsMyAnswer()
        }
        onReload()
    }

    private fun initView() {
        contentView.refreshLayout.setOnRefreshListener({
            contentView.refreshLayout.isRefreshing = false
            if (listStatus == ListStatus.Content) {
                onReload()
                listStatus = ListStatus.Refreshing
            } else {
                contentView.refreshLayout.isRefreshing = false
                toast("正在执行其他操作,请稍后再试")
            }
        })
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                when (type) {
                    TYPE_MYQUESTION -> {
                        if (TokenManage.isOauth()) {
                            viewModel.operateQuestionLiveData(
                                    TokenManage.getToken()!!.userId.toInt(),
                                    if (viewModel.questionList.size / viewModel.myQuestionLimit == 0)
                                        1
                                    else viewModel.questionList.size / viewModel.myQuestionLimit + 1
                            )
                            listStatus = ListStatus.LoadMore
                        } else showNeedOauth()
                    }
                    TYPE_MYANSWER -> {
                        if (userId != 0) {
                            viewModel.setAnswerLiveData(
                                    userId!!,
                                    if (viewModel.answerList.size / viewModel.myAnswerLimit == 0)
                                        1
                                    else viewModel.answerList.size / viewModel.myAnswerLimit + 1
                            )
                            listStatus = ListStatus.LoadMore
                        } else {
                            if (TokenManage.isOauth()) {
                                viewModel.setAnswerLiveData(
                                        TokenManage.getToken()!!.userId.toInt(),
                                        if (viewModel.answerList.size / viewModel.myAnswerLimit == 0)
                                            1
                                        else viewModel.answerList.size / viewModel.myAnswerLimit + 1
                                )
                                listStatus = ListStatus.LoadMore
                            } else showNeedOauth()
                        }
                    }
                }
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }
        }

    }

    override fun onReload() {
        if (type == TYPE_MYANSWER) {
            if (userId != 0) {
                viewModel.setAnswerLiveData(userId!!, 1)
            } else {
                if (TokenManage.isOauth()) {
                    viewModel.setAnswerLiveData(TokenManage.getToken()!!.userId.toInt(), 1)
                } else showNeedOauth()
            }
        } else if (type == TYPE_MYQUESTION) {
            if (TokenManage.isOauth()) {
                viewModel.operateQuestionLiveData(TokenManage.getToken()!!.userId.toInt(), 1)
            } else showNeedOauth()
        }
    }

    //我的问题页面
    private fun obsMyQuestion() {
        viewModel.obsMyQuestion()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.questionList.addAll(it.data!!)
                            } else {
                                viewModel.questionList = it.data!! as ArrayList<QuestionEntity?>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                                if (it.data!!.size < viewModel.myQuestionLimit)
                                    adapter.setLoadingNoMore()
                                else
                                    adapter.setLoadingSucceed()
                            }
                            if (it.data!!.size < viewModel.myQuestionLimit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()

                            applyQuestionData()
                            listStatus = ListStatus.Content
                            showContentView()
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
                                    if (it.error?.code == ErrorCode.EMPTY)
                                        showEmpty(it.error?.message)
                                    else
                                        showError(it.error?.message)
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

    private fun applyQuestionData() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.questionList.size
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        linearLayoutHelper.setDividerHeight(6.dp())
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        viewModel.questionList.forEach {
            adapter.diffItems.add(MyQuestionHolder(it)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance()
                                    .build(RouterPage.QuestionDetailsActivity)
                                    .withInt("id", myQuestionEntity?.id ?: 0)
                                    .navigation(activity)
                        }
                    })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as MyQuestionHolder).myQuestionEntity?.id ==
                            (baseItem1 as MyQuestionHolder).myQuestionEntity?.id
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as MyQuestionHolder).myQuestionEntity?.postTime ==
                            (baseItem1 as MyQuestionHolder).myQuestionEntity?.postTime
                }
        )
    }


    //    我的回答页面
    private fun obsMyAnswer() {
        viewModel.obsMyAnswer()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.answerList.addAll(it.data!!)
                                if (it.data!!.size < viewModel.myAnswerLimit)
                                    adapter.setLoadingNoMore()
                                else
                                    adapter.setLoadingSucceed()
                            } else {
                                viewModel.answerList = it.data!! as ArrayList<MyAnswerREntity?>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                                if (it.data!!.size < viewModel.myAnswerLimit)
                                    adapter.setLoadingNoMore()
                                else
                                    adapter.setLoadingSucceed()
                            }
                            (activity as? MyselfActivity)?.toolbar?.title = "我的回答(${viewModel.answerList.size})"
                            applyAnswerData()
                            listStatus = ListStatus.Content
                            showContentView()
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
                                    showError(it.error?.message)
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

    private fun applyAnswerData() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.answerList.size
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        linearLayoutHelper.setDividerHeight(6.dp())
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        viewModel.answerList.forEach { myAnswer ->
            adapter.diffItems.add(MyAnswerHolder(myAnswer)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance()
                                    .build(RouterPage.QuestionDetailsActivity)
                                    .withInt("id", myAnswer?.question?.id ?: 0)
                                    .navigation(activity)
                        }
                    }
            )
        }

        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as MyAnswerHolder).answerEntity?.id ==
                            (baseItem1 as MyAnswerHolder).answerEntity?.id
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as MyAnswerHolder).answerEntity?.replyTime ==
                            (baseItem1 as MyAnswerHolder).answerEntity?.replyTime
                }
        )

    }

}