package com.zzwl.farmingtrade.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.im.ImCreateHelp
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingViewRecyclerviewBinding
import com.zzwl.farmingtrade.room.entity.remote.FarmingProductItem
import com.zzwl.farmingtrade.room.entity.remote.PurchaseItem
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.purchase.holder.BuyHolder
import com.zzwl.farmingtrade.ui.supplier.holder.FarmingProductHolder
import com.zzwl.farmingtrade.viewModel.PurchaseHallViewModel
import com.zzwl.farmingtrade.viewModel.SupplierViewModel

/**
 * type : 8  采购搜索记录  9  农产品搜索记录
 */
@Route(path = RouterPage.TradeSearchResultActivity, extras = RouteExtras.NeedOauth)
class TradeSearchResultActivity : BaseActivity<FarmingViewRecyclerviewBinding>() {

    @Autowired
    @JvmField
    var keyword: String = ""

    @Autowired
    @JvmField
    var type: Int = 8
    private val purchaseViewModel by lazy { ViewModelProviders.of(this).get(PurchaseHallViewModel::class.java) }
    private val supplierViewModel by lazy { ViewModelProviders.of(this).get(SupplierViewModel::class.java) }
    private var listStatus = ListStatus.Content
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this@TradeSearchResultActivity)
        setContentView(R.layout.farming_view_recyclerview)
        initView()
        if (type == 8)
            obsPurchaseData()
        else
            obsSupplierData()
        onReload()
    }


    override fun onReload() {
        if (type == 8)
            purchaseViewModel.operatePurchase("1", purchaseViewModel.executedOrderBy, keyword, "")
        else
            supplierViewModel.operateSupplier("1", keyword, "", "")
    }

    private fun obsPurchaseData() {
        purchaseViewModel.obsPurchase()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                purchaseViewModel.purchaseItemList?.addAll(it.data!!.list)
                            } else {
                                purchaseViewModel.purchaseItemList = it.data!!.list as ArrayList<PurchaseItem>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < purchaseViewModel.limit)
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
                                    contentView.refreshLayout.isRefreshing = false
                                }
                                ListStatus.Content -> {
                                    showError(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }
                })

    }

    private fun obsSupplierData() {
        supplierViewModel.obsFarmingProduct()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                supplierViewModel.farmingProductList?.addAll(it.data!!.list)
                            } else {
                                supplierViewModel.farmingProductList = it.data!!.list as ArrayList<FarmingProductItem>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < supplierViewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            bindRecyclerView2()
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
                                    contentView.refreshLayout.isRefreshing = false
                                }
                                ListStatus.Content -> {
                                    showError(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }

                    }

                }

    }

    private fun bindRecyclerView2() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = supplierViewModel.farmingProductList?.size ?: 0
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        supplierViewModel.farmingProductList?.forEach {
            val imgUrl = if (it.imageList.isNotEmpty()) it.imageList[0].url else ""
            adapter.diffItems.add(FarmingProductHolder(it, imgUrl).apply {
                setOnClickListener {
                    ARouter.getInstance().build(RouterPage.SupplierDetailsActivity).withInt("id", farmingProductItem.id).navigation(this@TradeSearchResultActivity)
                }
            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->

                    (baseItem as FarmingProductHolder).farmingProductItem.id ==
                            (baseItem1 as FarmingProductHolder).farmingProductItem.id
                            && baseItem.farmingProductItem.quantity == baseItem1.farmingProductItem.quantity
                            && baseItem.farmingProductItem.price == baseItem1.farmingProductItem.price
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->

                    (baseItem as FarmingProductHolder).farmingProductItem.id ==
                            (baseItem1 as FarmingProductHolder).farmingProductItem.id
                            && baseItem.farmingProductItem.quantity == baseItem1.farmingProductItem.quantity
                            && baseItem.farmingProductItem.price == baseItem1.farmingProductItem.price
                }
        )
    }

    private fun initView() {
        toolbar.title = "搜索-$keyword"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        adapter.setOnLoadingListener {
            if (type == 8)
                loadingByPurchase()
            else
                loadingBySupplier()
        }
        contentView.refreshLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
                contentView.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun loadingBySupplier() {
        if (listStatus == ListStatus.Content) {
            val tempNum = if (supplierViewModel.farmingProductList?.size ?: 0 / supplierViewModel.limit == 0)
                1
            else
                (supplierViewModel.farmingProductList?.size ?: 0) / supplierViewModel.limit + 1
            supplierViewModel.operateSupplier(
                    tempNum.toString(),
                    keyword,
                    "",
                    supplierViewModel.executedOrderBy
            )

            listStatus = ListStatus.LoadMore
        } else {
            adapter.setLoadingFailed()
            toast("正在执行其他操作请稍后再试")
        }

    }

    private fun loadingByPurchase() {
        if (listStatus == ListStatus.Content) {
            val tempNum = if (purchaseViewModel.purchaseItemList?.size ?: 0 / purchaseViewModel.limit == 0)
                1
            else
                (purchaseViewModel.purchaseItemList?.size ?: 0) / purchaseViewModel.limit + 1
            purchaseViewModel.operatePurchase(
                    tempNum.toString(),
                    purchaseViewModel.executedOrderBy,
                    keyword,
                    ""
            )

            listStatus = ListStatus.LoadMore
        } else {
            adapter.setLoadingFailed()
            toast("正在执行其他操作请稍后再试")
        }

    }

    private fun bindRecyclerView() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = purchaseViewModel.purchaseItemList?.size ?: 0
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        purchaseViewModel.purchaseItemList?.forEach {
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


                        R.id.constrainLayout -> ARouter.getInstance().build(RouterPage.BuyDetailsActivity).withInt("id", purchaseItem.id).navigation(this@TradeSearchResultActivity)

                    }
                }
            })
        }
        adapter.calcDiffAndDispatchUpdate(
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->
                    (baseItem as BuyHolder).purchaseItem.id ==
                            (baseItem1 as BuyHolder).purchaseItem.id
                },
                { baseItem: BaseItem<*>, i: Int, baseItem1: BaseItem<*>, i1: Int ->

                    (baseItem as BuyHolder).purchaseItem.price ==
                            (baseItem1 as BuyHolder).purchaseItem.price
                }

        )
    }

}