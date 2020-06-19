package com.zzwl.farmingtrade.ui.purchase

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.ViewSlideBottom
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.im.ImCreateHelp
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivitySellHallBinding
import com.zzwl.farmingtrade.event.CropEvent
import com.zzwl.farmingtrade.room.entity.remote.PurchaseItem
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.purchase.holder.BuyHolder
import com.zzwl.farmingtrade.viewModel.PurchaseHallViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 *  quantity-desc   数量降序
 *  quantity-asc    数量升序
 *  price-desc      价格降序
 *  price-asc       价格升序
 */
@Route(path = RouterPage.PurchaseHallActivity, extras = RouteExtras.NeedOauth)
class PurchaseHallActivity : BaseActivity<FarmingActivitySellHallBinding>() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(PurchaseHallViewModel::class.java) }
    private var listStatus = ListStatus.Content
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }

    val amountSelectedObs by lazy { ObservableField(true) }
    val priceSelectedObs by lazy { ObservableField(false) }
    val categorySelectedObs by lazy { ObservableField(false) }

    val amountDescSelectedObs by lazy { ObservableField(false) }
    val amountAscSelectedObs by lazy { ObservableField(false) }
    val priceAscSelectedObs by lazy { ObservableField(false) }
    val priceDescSelectedObs by lazy { ObservableField(false) }
    private var lastSelectedObs: ObservableField<Boolean>? = null //4个升降序和一个分类中,上一次被选中的
    private var choiceCropId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_activity_sell_hall)
        initView()
        obsData()
        onReload()
        contentView.data = this
    }

    private fun initView() {
        contentView.imgBack.setOnClickListener {
            finish()
        }
        contentView.rlSearch.setOnClickListener {
            ARouter.getInstance().build(RouterPage.SearchActivity).withInt("type", 8).navigation(this)
        }
        contentView.tvAmount.setOnClickListener {
            amountSelectedObs.set(true)
            priceSelectedObs.set(false)
            categorySelectedObs.set(false)
            if (amountDescSelectedObs.get() == true) {
                setSelectedStatus(amountAscSelectedObs)
                viewModel.operatePurchase("1", "quantity-asc", cropId = "")
            } else {
                setSelectedStatus(amountDescSelectedObs)
                viewModel.operatePurchase("1", "quantity-desc", cropId = "")
            }
        }
        contentView.tvPrice.setOnClickListener {
            priceSelectedObs.set(true)
            amountSelectedObs.set(false)
            categorySelectedObs.set(false)
            if (priceDescSelectedObs.get() == true) {
                setSelectedStatus(priceAscSelectedObs)
                viewModel.operatePurchase("1", "price-asc", cropId = "")
            } else {
                setSelectedStatus(priceDescSelectedObs)
                viewModel.operatePurchase("1", "price-desc", cropId = "")
            }
        }
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                val tempNum = if (viewModel.purchaseItemList?.size ?: 0 / viewModel.limit == 0)
                    1
                else
                    (viewModel.purchaseItemList?.size ?: 0) / viewModel.limit + 1
                viewModel.operatePurchase(
                        tempNum.toString(),
                        viewModel.executedOrderBy,
                        cropId = choiceCropId
                )

                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }
        }
        contentView.swipeRefreshLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
                contentView.swipeRefreshLayout.isRefreshing = false
            }
        }
        contentView.tvCategory.setOnClickListener {
            categorySelectedObs.set(true)
            priceSelectedObs.set(false)
            amountSelectedObs.set(false)
            setSelectedStatus(categorySelectedObs)
            ARouter.getInstance().build(RouterPage.CropsActivity).withBoolean("isFollowed", false).navigation(this)
        }
        contentView.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ViewSlideBottom.animateIn(contentView.fab)
                } else {
                    ViewSlideBottom.animateOut(contentView.fab)
                }
            }
        })
        contentView.fab.setOnClickListener {
            ARouter.getInstance().build(RouterPage.PostSupplierActivity).navigation(this)
        }
        contentView.imgOrder.setOnClickListener {
            ARouter.getInstance().build(RouterPage.MyTradingActivity).navigation(this)
        }
    }

    private fun setSelectedStatus(selectedObs: ObservableField<Boolean>) {
        lastSelectedObs?.set(false)
        lastSelectedObs = selectedObs
        lastSelectedObs?.set(true)
    }

    override fun onReload() {
        if (lastSelectedObs == null)
            lastSelectedObs = amountDescSelectedObs
        lastSelectedObs?.set(true)
        viewModel.operatePurchase("1", viewModel.executedOrderBy, cropId = choiceCropId)
    }

    private fun obsData() {
        viewModel.obsPurchase()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.purchaseItemList?.addAll(it.data!!.list)
                            } else {
                                viewModel.purchaseItemList = it.data!!.list as ArrayList<PurchaseItem>
                                if (contentView.swipeRefreshLayout.isRefreshing)
                                    contentView.swipeRefreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < viewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            bindRecyclerView(listStatus)
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
                                ListStatus.Refreshing -> {
                                    contentView.swipeRefreshLayout.isRefreshing = false
                                }
                                ListStatus.Content -> {
                                    if (it.error?.code == ErrorCode.EMPTY) {
                                        viewModel.purchaseItemList?.clear()
                                        adapter.renderItems.clear()
                                        adapter.notifyDataSetChanged()
                                        showEmpty()
                                    } else
                                        showError(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }
                }
    }


    private fun bindRecyclerView(listStatusTemp: ListStatus) {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.purchaseItemList?.size ?: 0
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        viewModel.purchaseItemList?.forEach {
            adapter.diffItems.add(BuyHolder(it).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.tvTalk -> {
                            if (TokenManage.getToken()?.userId ?: 0 != purchaseItem.userId) {
                                ImCreateHelp.createWithPurchaseViewMessage(purchaseItem.price,
                                        purchaseItem.title,
                                        purchaseItem.quantity.toString(),
                                        purchaseItem.id.toString(),
                                        purchaseItem.userId.toString())
                            } else
                                toast("请选择自己之外的用户聊天")
                        }
                        R.id.constrainLayout -> ARouter.getInstance().build(RouterPage.BuyDetailsActivity).withInt("id", purchaseItem.id).navigation(this@PurchaseHallActivity)
                    }
                }

            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    if (listStatusTemp != ListStatus.LoadMore)
                        false
                    else
                        (baseItem as BuyHolder).purchaseItem.id ==
                                (baseItem1 as BuyHolder).purchaseItem.id
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    if (listStatusTemp != ListStatus.LoadMore)
                        false
                    else
                        (baseItem as BuyHolder).purchaseItem.price ==
                                (baseItem1 as BuyHolder).purchaseItem.price
                },
                {
                    if (listStatusTemp != ListStatus.LoadMore)
                        contentView.recyclerView.smoothScrollToPosition(0)

                }
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCropChoiceEvent(cropEvent: CropEvent) {
        choiceCropId = cropEvent.cropId
        onReload()
    }
}