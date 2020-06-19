package com.zzwl.farmingtrade.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.setMargin
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivitySearchBinding
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.search.holder.DividingHolder
import com.zzwl.farmingtrade.ui.search.holder.TagHolder
import com.zzwl.farmingtrade.ui.search.holder.TitleHolder
import com.zzwl.farmingtrade.viewModel.SearchViewModel

/**
 *  type : 8  采购搜索记录  9  农产品搜索记录
 *
 */
@Route(path = RouterPage.SearchActivity, extras = RouteExtras.NeedOauth)
class SearchActivity : BaseActivity<FarmingActivitySearchBinding>() {

    @Autowired
    @JvmField
    var type: Int = 0

    override var isFullScreen: Boolean = true
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private var listStatus = ListStatus.Content
    private val viewModel by lazy { ViewModelProviders.of(this).get(SearchViewModel::class.java) }
    private val searchHistoryHelper by lazy { SearchHistoryHelper(this, type) }
    private val delObs by lazy { ObservableField(false) }
    val hintObs by lazy { ObservableField("请输入作物名称,产地,供应商进行搜索") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_activity_search)
        ARouter.getInstance().inject(this)
        initView()
        contentView.data = this
        when (type) {
            8 -> {
                getDataByPurchase()
            }
            9 -> {
                getDataBySupplier()
                hintObs.set("请输入作物名称,产地,采购商进行搜索")
            }
        }

    }

    private fun getDataByPurchase() {
        viewModel.getHotSearchWordByPurchase()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            viewModel.wordList.addAll(it.data!!)
                            initSearchView(false)
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.Content -> {
                                    showEmpty(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }


                }
    }

    private fun getDataBySupplier() {
        viewModel.getHotSearchWordBySupplier()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            viewModel.wordList.addAll(it.data!!)
                            initSearchView(false)
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.Content -> {
                                    showEmpty(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }


                })

    }

    private fun initView() {
        contentView.recyclerView.itemAnimator = DefaultItemAnimator()
        contentView.constraintLayout.setMargin(statusBarHeight, 0, 0, 0)
        contentView.edtSearch.setOnEditorActionListener { view, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                ARouter.getInstance().build(RouterPage.TradeSearchResultActivity).withInt("type", type).withString("keyword", view.text.toString()).navigation(this@SearchActivity)
                searchHistoryHelper.save(view.text.toString())
                initSearchView(false)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        contentView.tvCancel.setOnClickListener {
            contentView.edtSearch.setText("")
        }
    }


    private fun initSearchView(isDelete: Boolean) {
        val historyData = searchHistoryHelper.getAll()
        val keyArray = historyData.keys.toTypedArray()

        if (historyData.isNotEmpty()) delObs.set(true) else delObs.set(false)
        val linearLayout = LinearLayoutHelper()
        linearLayout.itemCount = 1

        val linearLayout1 = LinearLayoutHelper()
        linearLayout1.itemCount = 1

        val linearLayout2 = LinearLayoutHelper()
        linearLayout2.itemCount = 1
        linearLayout2.marginTop = 16.dp()

        val gridLayoutHelper = GridLayoutHelper(4, viewModel.wordList.size)
        gridLayoutHelper.hGap = 8.dp()
        gridLayoutHelper.vGap = 12.dp()
        gridLayoutHelper.setAutoExpand(false)
        gridLayoutHelper.setMargin(16.dp(), 0, 16.dp(), 0)

        val historyGridLayoutHelper = GridLayoutHelper(4, historyData.size)
        historyGridLayoutHelper.hGap = 8.dp()
        historyGridLayoutHelper.vGap = 12.dp()
        historyGridLayoutHelper.setAutoExpand(false)
        historyGridLayoutHelper.setMargin(16.dp(), 0, 16.dp(), 0)
        val helperList = listOf<LayoutHelper>(linearLayout, gridLayoutHelper, linearLayout2, linearLayout1, historyGridLayoutHelper)

        adapter.diffItems.add(TitleHolder("热门搜索"))

        viewModel.wordList.forEach {
            adapter.diffItems.add(TagHolder(it)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance()
                                    .build(RouterPage.TradeSearchResultActivity)
                                    .withString("keyword", tag)
                                    .withInt("type", type)
                                    .navigation(this@SearchActivity)
                        }
                    })
        }
        adapter.diffItems.add(DividingHolder())
        adapter.diffItems.add(TitleHolder("历史搜索", delObs)
                .apply {
                    setOnClickListener {
                        searchHistoryHelper.removeAll()
                        initSearchView(true)
                    }
                })
        keyArray.sortedDescending().forEach {
            adapter.diffItems.add(TagHolder(historyData[it] as String)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance()
                                    .build(RouterPage.TradeSearchResultActivity)
                                    .withString("keyword", tag)
                                    .withInt("type", type)
                                    .navigation(this@SearchActivity)
                        }
                    })
        }
        if (!isDelete)
            adapter.layoutHelpers = helperList
        adapter.calcDiffAndDispatchUpdate(
                { oldItem: BaseItem<*>, oldIndex: Int, newItem: BaseItem<*>, newIndex: Int ->
                    when {
                        oldItem is TagHolder && newItem is TagHolder -> {
                            oldItem.tag == newItem.tag
                        }
                        oldItem is TitleHolder && newItem is TitleHolder -> {
                            oldItem.title == newItem.title
                        }
                        else -> false
                    }
                },
                { oldItem: BaseItem<*>, oldIndex: Int, newItem: BaseItem<*>, newIndex: Int ->
                    when {
                        oldItem is TagHolder && newItem is TagHolder -> {
                            oldItem.tag == newItem.tag
                        }
                        oldItem is TitleHolder && newItem is TitleHolder -> {
                            oldItem.title == newItem.title
                        }
                        else -> false
                    }
                })
    }
}