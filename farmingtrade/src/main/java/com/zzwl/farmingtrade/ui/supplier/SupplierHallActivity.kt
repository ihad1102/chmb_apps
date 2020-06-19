package com.zzwl.farmingtrade.ui.supplier

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
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivitySupplierHallBinding
import com.zzwl.farmingtrade.event.CropEvent
import com.zzwl.farmingtrade.room.entity.remote.FarmingProductItem
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.supplier.holder.FarmingProductHolder
import com.zzwl.farmingtrade.viewModel.SupplierViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPage.SupplierHallActivity, extras = RouteExtras.NeedOauth)
class SupplierHallActivity : BaseActivity<FarmingActivitySupplierHallBinding>() {
    val viewModel by lazy { ViewModelProviders.of(this).get(SupplierViewModel::class.java) }
    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private var listStatus = ListStatus.Content

    val amountSelectedObs by lazy { ObservableField(true) }
    val priceSelectedObs by lazy { ObservableField(false) }
    val categorySelectedObs by lazy { ObservableField(false) }

    val amountDescSelectedObs by lazy { ObservableField(false) }
    val amountAscSelectedObs by lazy { ObservableField(false) }
    val priceAscSelectedObs by lazy { ObservableField(false) }
    val priceDescSelectedObs by lazy { ObservableField(false) }
    private var lastSelectedObs: ObservableField<Boolean>? = null //4个升降序和一个分类中,上一次被选中的
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_activity_supplier_hall)
        initView()
        obsData()
        onReload()
        contentView.data = this
    }

    private fun initView() {
        contentView.imgBack.setOnClickListener {
            finish()
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
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                val tempNum = if (viewModel.farmingProductList?.size ?: 0 / viewModel.limit == 0)
                    1
                else
                    (viewModel.farmingProductList?.size ?: 0) / viewModel.limit + 1
                viewModel.operateSupplier(
                        tempNum.toString(),
                        cropId = viewModel.choiceCropId,
                        orderBy = viewModel.executedOrderBy
                )

                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }
        }
        contentView.fab.setOnClickListener {
            ARouter.getInstance().build(RouterPage.PostPurchaseActivity).navigation(this)
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
        contentView.rlSearch.setOnClickListener {
            ARouter.getInstance().build(RouterPage.SearchActivity).withInt("type", 9).navigation(this)
        }
        contentView.imgOrder.setOnClickListener {
            ARouter.getInstance().build(RouterPage.MyTradingActivity).navigation(this)
        }
        contentView.tvAmount.setOnClickListener {
            amountSelectedObs.set(true)
            priceSelectedObs.set(false)
            categorySelectedObs.set(false)
            if (amountDescSelectedObs.get() == true) {
                setSelectedStatus(amountAscSelectedObs)
                viewModel.executedOrderBy = "quantity-asc"
                viewModel.operateSupplier("1", cropId = "", orderBy = viewModel.executedOrderBy)
            } else {
                setSelectedStatus(amountDescSelectedObs)
                viewModel.executedOrderBy = "quantity-desc"
                viewModel.operateSupplier("1", cropId = "", orderBy = viewModel.executedOrderBy)
            }
        }
        contentView.tvPrice.setOnClickListener {
            priceSelectedObs.set(true)
            amountSelectedObs.set(false)
            categorySelectedObs.set(false)
            if (priceDescSelectedObs.get() == true) {
                setSelectedStatus(priceAscSelectedObs)
                viewModel.executedOrderBy = "price-asc"
                viewModel.operateSupplier("1", cropId = "", orderBy = viewModel.executedOrderBy)
            } else {
                setSelectedStatus(priceDescSelectedObs)
                viewModel.executedOrderBy = "price-desc"
                viewModel.operateSupplier("1", cropId = "", orderBy = viewModel.executedOrderBy)
            }
        }
        contentView.tvCategory.setOnClickListener {
            categorySelectedObs.set(true)
            priceSelectedObs.set(false)
            amountSelectedObs.set(false)
            setSelectedStatus(categorySelectedObs)
            ARouter.getInstance().build(RouterPage.CropsActivity).withBoolean("isFollowed", false).navigation(this)
        }

    }

    private fun setSelectedStatus(selectedObs: ObservableField<Boolean>) {
        lastSelectedObs?.set(false)
        lastSelectedObs = selectedObs
        lastSelectedObs?.set(true)
    }

    private fun obsData() {
        viewModel.obsFarmingProduct()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.farmingProductList?.addAll(it.data!!.list)
                            } else {
                                viewModel.farmingProductList = it.data!!.list as ArrayList<FarmingProductItem>
                                if (contentView.swipeRefreshLayout.isRefreshing)
                                    contentView.swipeRefreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < viewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            bindRecyclerView()
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
                                    viewModel.farmingProductList?.clear()
                                    adapter.renderItems.clear()
                                    adapter.notifyDataSetChanged()
                                }
                                ListStatus.Content -> {
                                    if (it.error?.code == ErrorCode.EMPTY) {
                                        showEmpty()
                                    } else
                                        showError(it.error?.message)
                                    adapter.renderItems.clear()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }

                }
    }

    private fun bindRecyclerView() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.farmingProductList?.size ?: 0
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        viewModel.farmingProductList?.forEach {
            val imgUrl = if (it.imageList.isNotEmpty()) it.imageList[0].url else ""
            adapter.diffItems.add(FarmingProductHolder(it, imgUrl).apply {
                setOnClickListener {
                    ARouter.getInstance().build(RouterPage.SupplierDetailsActivity).withInt("id", farmingProductItem.id).navigation(this@SupplierHallActivity)
                }
            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->

                    (baseItem as FarmingProductHolder).farmingProductItem.id ==
                            (baseItem1 as FarmingProductHolder).farmingProductItem.id
                            && baseItem.farmingProductItem.quantity == baseItem1.farmingProductItem.quantity
                            && baseItem.farmingProductItem.price == baseItem1.farmingProductItem.price
                            && baseItem.farmingProductItem.address == baseItem1.farmingProductItem.address
                            && baseItem.farmingProductItem.specification == baseItem1.farmingProductItem.specification
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->

                    (baseItem as FarmingProductHolder).farmingProductItem.id ==
                            (baseItem1 as FarmingProductHolder).farmingProductItem.id
                            && baseItem.farmingProductItem.quantity == baseItem1.farmingProductItem.quantity
                            && baseItem.farmingProductItem.price == baseItem1.farmingProductItem.price
                            && baseItem.farmingProductItem.address == baseItem1.farmingProductItem.address
                            && baseItem.farmingProductItem.specification == baseItem1.farmingProductItem.specification
                }
        )
    }

    override fun onReload() {
        if (lastSelectedObs == null)
            lastSelectedObs = amountDescSelectedObs
        lastSelectedObs?.set(true)
        viewModel.operateSupplier("1", cropId = viewModel.choiceCropId, orderBy = viewModel.executedOrderBy)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCropChoiceEvent(cropEvent: CropEvent) {
        viewModel.choiceCropId = cropEvent.cropId
        onReload()
    }
}