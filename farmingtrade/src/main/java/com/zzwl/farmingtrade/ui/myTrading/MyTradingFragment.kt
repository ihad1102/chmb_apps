package com.zzwl.farmingtrade.ui.myTrading


import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.alipay.sdk.app.PayTask
import com.g.base.api.ErrorCode
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.help.CallPhone
import com.g.base.help.ProgressDialog
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.farmingtrade.BR
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.api.PurchaseApi
import com.zzwl.farmingtrade.api.SupplierApi
import com.zzwl.farmingtrade.databinding.FarmingDialogPayWayMytradingBinding
import com.zzwl.farmingtrade.databinding.FarmingViewRecyclerviewBinding
import com.zzwl.farmingtrade.event.RefreshTradingEvent
import com.zzwl.farmingtrade.extend.progressOauthSubscribe
import com.zzwl.farmingtrade.room.entity.remote.MyPurchaseItem
import com.zzwl.farmingtrade.room.entity.remote.MySupplierItem
import com.zzwl.farmingtrade.room.entity.remote.MyTradeItem
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.myTrading.holder.MyPurchaseHolder
import com.zzwl.farmingtrade.ui.myTrading.holder.MySupplierHolder
import com.zzwl.farmingtrade.ui.myTrading.holder.MyTradingHolder
import com.zzwl.farmingtrade.viewModel.MyTradeViewModel
import com.zzwl.farmingtrade.viewModel.TradePayViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * type: 1 我的供应
 *       2 我的采购
 *       3 我的交易
 */
class MyTradingFragment : BaseFragment<FarmingViewRecyclerviewBinding>() {
    private var listStatus = ListStatus.Content
    private val progressDialog by lazy { ProgressDialog() }

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(MyTradeViewModel::class.java) }
    val tradePayViewModel by lazy { ViewModelProviders.of(activity!!).get(TradePayViewModel::class.java) }
    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    var userType = 0 //0,1 :农民,采购商
    private val payWayObs = ObservableField<String>("支付宝")
    val checkedPayWay = ObservableField(0)              //被选中的支付方式
    var paymentType: Int = 1                                    //配送方式, 1:在线支付,2:货到付款,3:预付

    private var dealId = "0"
    private lateinit var statusTempObs: ObservableField<String>
    private lateinit var btnTempObs: ObservableField<String>
    private lateinit var myTradeItem: MyTradeItem
    override fun setContentView(): Int = R.layout.farming_view_recyclerview
    override fun onLazyLoadOnce() {
        when (arguments!!.getInt("type", 1)) {
            1 -> getMySupplierData()
            2 -> getMyPurchaseData()
            3 -> {
                getMyTradingData()
                getSharePreference()
            }
        }
        initView()
        onReload()
    }

    private fun getSharePreference() {
        userType = context!!.getSharedPreferences("userType", Context.MODE_PRIVATE).getInt("userType", 0)
    }

    private fun initView() {
        contentView.refreshLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
                contentView.refreshLayout.isRefreshing = false
            }
        }
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                when (arguments!!.getInt("type")) {
                    1 -> viewModel.operateMySupplier((viewModel.mySupplierList?.size
                            ?: 0) / viewModel.limit + 1)
                    2 -> viewModel.operateMyPurchase((viewModel.myPurchaseList?.size
                            ?: 0) / viewModel.limit + 1)
                    3 -> viewModel.operateMyTrading((viewModel.myTradingList?.size
                            ?: 0) / viewModel.limit + 1)
                }
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }


        }
    }

    override fun onReload() {
        when (arguments!!.getInt("type")) {
            1 -> viewModel.operateMySupplier(1)
            2 -> viewModel.operateMyPurchase(1)
            3 -> viewModel.operateMyTrading(1)
        }
    }

    private fun getMySupplierData() {
        viewModel.obsMySupplier()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.mySupplierList?.addAll(it.data!!.list)
                            } else {
                                viewModel.mySupplierList = it.data!!.list as ArrayList<MySupplierItem>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
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

    private fun getMyPurchaseData() {
        viewModel.obsMyPurchase()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.myPurchaseList?.addAll(it.data!!.list)
                            } else {
                                viewModel.myPurchaseList = it.data!!.list as ArrayList<MyPurchaseItem>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < viewModel.limit)
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

    private fun getMyTradingData() {
        viewModel.obsMyTrading()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            if (listStatus == ListStatus.LoadMore) {
                                viewModel.myTradingList?.addAll(it.data!!.list)
                            } else {
                                viewModel.myTradingList = it.data!!.list as ArrayList<MyTradeItem>
                                if (contentView.refreshLayout.isRefreshing)
                                    contentView.refreshLayout.isRefreshing = false
                            }
                            if (it.data!!.list.size < viewModel.limit)
                                adapter.setLoadingNoMore()
                            else
                                adapter.setLoadingSucceed()
                            bindRecyclerView3()
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


    private fun bindRecyclerView() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.mySupplierList?.size ?: 0
        linearLayoutHelper.setDividerHeight(8.dp())
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        viewModel.mySupplierList?.forEach {
            val tempBtnObs = ObservableField("上架")
            val isShowBtn2Obs = ObservableField(false)
            val isShowBtn3Obs = ObservableField(false)
            val tempStatus = when {
                it.quantity == "0" -> {
                    tempBtnObs.set("删除")
                    isShowBtn2Obs.set(false)
                    isShowBtn3Obs.set(false)
                    "已售罄"
                }
                it.isPutaway -> {
                    tempBtnObs.set("下架")
                    isShowBtn2Obs.set(true)
                    isShowBtn3Obs.set(false)
                    "在售中"
                }
                else -> {
                    tempBtnObs.set("上架")
                    isShowBtn2Obs.set(true)
                    isShowBtn3Obs.set(true)
                    "已下架"
                }
            }
            val tempImg = if (it.imageList.isNotEmpty()) it.imageList[0].url else ""
            adapter.diffItems.add(MySupplierHolder(
                    tempImg,
                    it,
                    ObservableField(tempStatus),
                    tempBtnObs,
                    isShowBtn2Obs,
                    isShowBtn3Obs).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.tvBtn1 -> {
                            when (statusObs.get()) {
                                "在售中" -> //下架
                                    openDialog("您是否要下架?") {
                                        apiProvider.create(SupplierApi::class.java)
                                                .unShelveSupplier(mySupplierItem.id.toString())
                                                .mainIoSchedulers()
                                                .progressOauthSubscribe(onNext = {
                                                    tempBtnObs.set("上架")
                                                    statusObs.set("已下架")
                                                    isShowBtn3Obs.set(true)
                                                })
                                    }
                                "已售罄" -> { //删除
                                    openDialog("您是否要删除") {
                                        apiProvider.create(SupplierApi::class.java)
                                                .deleteFarmingProduct(mySupplierItem.id.toString())
                                                .mainIoSchedulers()//TODO 测试
                                                .progressOauthSubscribe(onNext = {
                                                    val index = adapter.renderItems.indexOfFirst { it === this }
                                                    if (index != -1) {
                                                        adapter.renderItems.removeAt(index)
                                                        if (adapter.renderItems.size == 0)
                                                            adapter.notifyDataSetChanged()
                                                        else
                                                            adapter.notifyItemRemoved(index)
                                                    }
                                                })
                                    }
                                }
                                else -> //上架
                                    apiProvider.create(SupplierApi::class.java)
                                            .putawaySupplier(mySupplierItem.id.toString())
                                            .mainIoSchedulers()
                                            .progressOauthSubscribe(onNext = {
                                                tempBtnObs.set("下架")
                                                statusObs.set("在售中")
                                                isShowBtn3Obs.set(false)
                                            })
                            }
                        }
                        R.id.constrainLayout -> ARouter.getInstance().build(RouterPage.SupplierDetailsActivity).withInt("id", mySupplierItem.id).navigation(context)
                        R.id.tvBtn2 -> {
                            ARouter.getInstance().build(RouterPage.PostSupplierActivity).withString("id", mySupplierItem.id.toString()).withInt("type", 1).navigation(context)
                        }
                        R.id.tvBtn3 -> {
                            openDialog("您是否要删除") {
                                apiProvider.create(SupplierApi::class.java)
                                        .deleteFarmingProduct(mySupplierItem.id.toString())
                                        .mainIoSchedulers()//todo 测试
                                        .progressOauthSubscribe(onNext = {
                                            val index = adapter.renderItems.indexOfFirst { it === this }
                                            if (index != -1) {
                                                adapter.renderItems.removeAt(index)
                                                if (adapter.renderItems.size == 0)
                                                    adapter.notifyDataSetChanged()
                                                else
                                                    adapter.notifyItemRemoved(index)
                                            }
                                        })
                            }
                        }
                    }
                }
            })
        }
        adapter.layoutHelpers = listOf(linearLayoutHelper)
        adapter.calcDiffAndDispatchUpdate(
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    (old as MySupplierHolder).mySupplierItem.isPutaway == (new as MySupplierHolder).mySupplierItem.isPutaway &&
                            old.mySupplierItem.id == new.mySupplierItem.id &&
                            old.mySupplierItem.title == new.mySupplierItem.title &&
                            old.mySupplierItem.price == new.mySupplierItem.price &&
                            old.mySupplierItem.quantity == new.mySupplierItem.quantity
                },
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    (old as MySupplierHolder).mySupplierItem.isPutaway == (new as MySupplierHolder).mySupplierItem.isPutaway &&
                            old.mySupplierItem.id == new.mySupplierItem.id &&
                            old.mySupplierItem.title == new.mySupplierItem.title &&
                            old.mySupplierItem.price == new.mySupplierItem.price &&
                            old.mySupplierItem.quantity == new.mySupplierItem.quantity
                }
        )
    }

    private fun bindRecyclerView2() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.myPurchaseList?.size ?: 0
        linearLayoutHelper.setDividerHeight(8.dp())
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        viewModel.myPurchaseList?.forEach {
            val tempStatusObs = ObservableField(it.doing)
            adapter.diffItems.add(MyPurchaseHolder(it, tempStatusObs).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.tvBtn1 -> {
                            if (tempStatusObs.get() == true) {
                                openDialog("您是否要下架") {
                                    apiProvider.create(PurchaseApi::class.java)
                                            .unShelvePurchase(myPurchaseItem.id.toString())
                                            .mainIoSchedulers()
                                            .progressOauthSubscribe(onNext = {
                                                tempStatusObs.set(false)
                                            })
                                }
                            } else {
                                apiProvider.create(PurchaseApi::class.java)
                                        .putawayPurchase(myPurchaseItem.id.toString())
                                        .mainIoSchedulers()
                                        .progressOauthSubscribe(onNext = {
                                            tempStatusObs.set(true)
                                        })
                            }
                        }
                        R.id.tvBtn3 -> openDialog("您是否要删除") {
                            apiProvider.create(PurchaseApi::class.java)
                                    .deletePurchase(myPurchaseItem.id.toString())
                                    .mainIoSchedulers() //todo  测试
                                    .progressOauthSubscribe(onNext = {
                                        val index = adapter.renderItems.indexOfFirst { it === this }
                                        if (index != -1) {
                                            adapter.renderItems.removeAt(index)
                                            if (adapter.renderItems.size == 0)
                                                adapter.notifyDataSetChanged()
                                            else
                                                adapter.notifyItemRemoved(index)
                                        }
                                    })
                        }
                        R.id.tvBtn2 -> ARouter.getInstance().build(RouterPage.PostPurchaseActivity).withString("id", myPurchaseItem.id.toString()).withInt("type", 1).navigation(context)
                        R.id.constrainLayout -> ARouter.getInstance().build(RouterPage.BuyDetailsActivity).withInt("id", myPurchaseItem.id).navigation(context)
                    }
                }
            })
        }
        adapter.layoutHelpers = listOf(linearLayoutHelper)
        adapter.calcDiffAndDispatchUpdate(
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    (old as MyPurchaseHolder).myPurchaseItem.doing == (new as MyPurchaseHolder).myPurchaseItem.doing &&
                            old.myPurchaseItem.id == new.myPurchaseItem.id &&
                            old.myPurchaseItem.title == new.myPurchaseItem.title &&
                            old.myPurchaseItem.price == new.myPurchaseItem.price &&
                            old.myPurchaseItem.quantity == new.myPurchaseItem.quantity
                },
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    (old as MyPurchaseHolder).myPurchaseItem.doing == (new as MyPurchaseHolder).myPurchaseItem.doing &&
                            old.myPurchaseItem.id == new.myPurchaseItem.id &&
                            old.myPurchaseItem.title == new.myPurchaseItem.title &&
                            old.myPurchaseItem.price == new.myPurchaseItem.price &&
                            old.myPurchaseItem.quantity == new.myPurchaseItem.quantity
                }
        )
    }

    private fun bindRecyclerView3() {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.myTradingList?.size ?: 0
        linearLayoutHelper.setDividerHeight(8.dp())
        linearLayoutHelper.setMargin(12.dp(), 12.dp(), 12.dp(), 12.dp())
        viewModel.myTradingList?.forEach {
            val id = addZeroForNum(it.id, 6)
            val statusTempObs = ObservableField("")
            val btnTempObs = ObservableField("")
            if (userType == 0) {
                when (it.status) {
                    0 -> statusTempObs.set("等待确认")
                    1 -> statusTempObs.set("等待采购方付款")
                    2 -> statusTempObs.set("待平台结算")
                    3 -> statusTempObs.set("交易取消")
                    4 -> statusTempObs.set("交易完成")
                }
                when (it.status) {
                    0 -> {
                        btnTempObs.set("确认交易")
                    }
                    1 -> {
                        btnTempObs.set("取消交易")
                    }
                    else -> {
                        btnTempObs.set("联系客服")
                    }
                }
            } else {
                when (it.status) {
                    0 -> statusTempObs.set("等待卖家确认")
                    1 -> statusTempObs.set("等待付款")
                    2 -> statusTempObs.set("交易完成")
                    3 -> statusTempObs.set("交易取消")
                    4 -> statusTempObs.set("交易完成")
                }
                when (it.status) {
                    0 -> {
                        btnTempObs.set("取消交易")
                    }
                    1 -> {
                        btnTempObs.set("立即支付")
                    }
                    else -> {
                        btnTempObs.set("联系客服")
                    }
                }

            }
            val tempIsShow = if (userType == 0) it.status == 0 else false
            val isShowBtn2Obs = ObservableField(tempIsShow)
            adapter.diffItems.add(MyTradingHolder(it, id, statusTempObs, btnTempObs, userType, isShowBtn2Obs).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.tvBtn1 -> {
                            onClickByBtn1(btnTempObs, statusTempObs)
                        }
                        R.id.tvBtn2 -> {
                            openDialog("是否要取消交易") {
                                apiProvider.create(PurchaseApi::class.java)
                                        .cancelTrade(myTradeItem.id)
                                        .mainIoSchedulers()
                                        .progressOauthSubscribe(onNext = {
                                            statusTempObs.set("交易取消")
                                            btnTempObs.set("联系客服")
                                            isShowBtn2Obs.set(false)
                                        })
                            }
                        }
                    }
                }
            })
        }
        adapter.layoutHelpers = listOf(linearLayoutHelper)
        adapter.calcDiffAndDispatchUpdate(
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    val tempUserOld = if (userType == 0) {
                        (old as MyTradingHolder).myTradeItem.buyUser.realname
                    } else {
                        (old as MyTradingHolder).myTradeItem.sellUser.realname
                    }
                    val tempUserNew = if (userType == 0) {
                        (new as MyTradingHolder).myTradeItem.buyUser.realname
                    } else {
                        (new as MyTradingHolder).myTradeItem.sellUser.realname
                    }
                    old.myTradeItem.status == new.myTradeItem.status &&
                            old.myTradeItem.id == new.myTradeItem.id &&
                            old.myTradeItem.crop.name == new.myTradeItem.crop.name &&
                            old.myTradeItem.weight == new.myTradeItem.weight &&
                            old.myTradeItem.totalPrice == new.myTradeItem.totalPrice &&
                            tempUserOld == tempUserNew
                },
                { old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int ->
                    val tempUserOld = if (userType == 0) {
                        (old as MyTradingHolder).myTradeItem.buyUser.realname
                    } else {
                        (old as MyTradingHolder).myTradeItem.sellUser.realname
                    }
                    val tempUserNew = if (userType == 0) {
                        (new as MyTradingHolder).myTradeItem.buyUser.realname
                    } else {
                        (new as MyTradingHolder).myTradeItem.sellUser.realname
                    }
                    old.myTradeItem.status == new.myTradeItem.status &&
                            old.myTradeItem.id == new.myTradeItem.id &&
                            old.myTradeItem.crop.name == new.myTradeItem.crop.name &&
                            old.myTradeItem.weight == new.myTradeItem.weight &&
                            old.myTradeItem.totalPrice == new.myTradeItem.totalPrice &&
                            tempUserOld == tempUserNew
                }
        )
    }

    private fun MyTradingHolder.onClickByBtn1(btnTempObs: ObservableField<String>, statusTempObs: ObservableField<String>) {
        when (myTradeItem.status) {
            0 -> {
                if (userType == 0) {
                    openDialog("是否确认交易") {
                        apiProvider.create(PurchaseApi::class.java)
                                .confirmTrade(myTradeItem.id)
                                .mainIoSchedulers()
                                .progressOauthSubscribe(onNext = {
                                    statusTempObs.set("等待采购方付款")
                                    btnTempObs.set("取消交易")
                                    isShowBtn2Obs.set(false)
                                    myTradeItem.status = 1
                                })
                    }
                } else {
                    openDialog("是否要取消交易") {
                        apiProvider.create(PurchaseApi::class.java)
                                .cancelTrade(myTradeItem.id)
                                .mainIoSchedulers()
                                .progressOauthSubscribe(onNext = {
                                    statusTempObs.set("交易取消")
                                    btnTempObs.set("联系客服")
                                    myTradeItem.status = 3
                                })
                    }

                }
            }
            1 -> {
                if (userType == 0) {
                    openDialog("是否要取消交易") {
                        apiProvider.create(PurchaseApi::class.java)
                                .cancelTrade(myTradeItem.id)
                                .mainIoSchedulers()
                                .progressOauthSubscribe(onNext = {
                                    statusTempObs.set("交易取消")
                                    btnTempObs.set("联系客服")
                                    myTradeItem.status = 3
                                })
                    }
                } else {
                    this@MyTradingFragment.statusTempObs = statusTempObs
                    dealId = myTradeItem.id
                    this@MyTradingFragment.myTradeItem = myTradeItem
                    this@MyTradingFragment.btnTempObs = btnTempObs
//                    toPay(myTradeItem.id, statusTempObs, btnTempObs, myTradeItem)
                    openPayWayDialog()
                }
            }
            else -> {
                CallPhone.callTel(activity!!, "029-88188685")
            }
        }

    }

    private fun openDialog(msg: String, call: () -> Unit) {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(msg)
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton("确定") { dialog, _ ->
            call()
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    //左补0
    fun addZeroForNum(str: String, strLength: Int): String {
        var str1 = str
        var strLen = str.length
        if (strLen < strLength) {
            while (strLen < strLength) {
                val sb = StringBuffer()
                sb.append("0").append(str1)//左补0
                str1 = sb.toString()
                strLen = str1.length
            }
        }
        return str1
    }

    var inflateDataBinding: FarmingDialogPayWayMytradingBinding? = null
    private lateinit var alertDialog: AlertDialog
    private fun openPayWayDialog() {
        inflateDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.farming_dialog_pay_way_mytrading, null, false)
        inflateDataBinding!!.setVariable(BR.data, this)
        alertDialog = AlertDialog.Builder(context!!).setView(inflateDataBinding!!.root).create()
        alertDialog.show()

    }

    fun onPayWaySelectChangeEvent(view: View) {
        when (view.id) {
            R.id.llAliPay -> {
                payWayObs.set("支付宝")
                checkedPayWay.set(0)
                paymentType = 1
                toPay(dealId, statusTempObs, btnTempObs, myTradeItem)
            }
            R.id.llWechat -> {
                payWayObs.set("微信")
                checkedPayWay.set(1)
                paymentType = 1
            }
        }
        inflateDataBinding?.executePendingBindings()
        setTimeout(200L) {
            alertDialog.dismiss()
        }
    }

    private fun toPay(dealId: String, statusTempObs: ObservableField<String>, btnTempObs: ObservableField<String>, myTradeItem: MyTradeItem) {
        tradePayViewModel.getPayParams(dealId)
                .resultNotNull()
                .observeExOnce(this) {
                    when (it.status) {
                        Status.Loading -> {
                            progressDialog.setStart("正在获取支付参数中...")
                        }
                        Status.Content -> {
                            invokeAlipay(it.data!!, dealId, statusTempObs, btnTempObs, myTradeItem)
                        }
                        Status.Error -> {
                            progressDialog.setFiled("获取支付参数失败 无法支付~") {
                                it.dismiss(true)
                            }
                        }
                    }
                }

    }

    /**
     * 通过支付宝支付
     */
    @SuppressLint("CheckResult")
    private fun invokeAlipay(payParams: String, dealId: String, statusTempObs: ObservableField<String>, btnTempObs: ObservableField<String>, myTradeItem: MyTradeItem) {
        progressDialog.dialog?.contentText = "正在调用支付宝~"
        io.reactivex.Observable.create<Map<String, String>> { ob ->
            ob.onNext(PayTask(activity!!).payV2(payParams, false))
        }
                .mainIoSchedulers()
                .subscribe(
                        { result ->
                            when (result["resultStatus"]) {
                                "9000", "8000", "6004", "5000" -> {
                                    asyncPayResult(dealId, myTradeItem, btnTempObs, statusTempObs)
                                }
                                else -> {
                                    progressDialog.setFiled(result["msg"]
                                            ?: "未知原因 支付失败 Code:${result["resultStatus"]}")
                                    {
                                        it.dismiss(true)
                                    }
                                }
                            }
                        },
                        {
                            progressDialog.setFiled("支付宝支付失败") {
                                it.dismiss(true)
                            }
                        }
                )
    }

    @SuppressLint("CheckResult")
    private fun asyncPayResult(dealId: String, myTradeItem: MyTradeItem, btnTempObs: ObservableField<String>, statusTempObs: ObservableField<String>) {
        tradePayViewModel.asyncStatus(dealId)
                .toObservable(this)
                .retryWithDelay(3, 1000L)
                .subscribe(
                        {
                            when (it.status) {
                                Status.Loading -> {
                                    progressDialog.dialog?.titleText = "获取支付结果中,请勿关闭"
                                }
                                Status.Content -> {
                                    //TODO 交易成功
                                    myTradeItem.status = 2
                                    btnTempObs.set("联系客服")
                                    statusTempObs.set("交易完成")
                                    progressDialog.setSucceed("支付成功") {
                                        it.dismiss()
                                    }
                                }
                                Status.Error -> {
                                    //TODO 交易失败
                                    progressDialog.setFiled(it.error!!.message) {
                                        it.dismiss()
                                    }
                                }
                            }
                        },
                        {
                            //TODO 交易失败
                            progressDialog.setFiled("暂时无法查询到啊交易结果,请稍后刷新本页面,或者联系客服!") {
                                it.dismiss(true)
                            }
                        }
                )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(refreshEvent: RefreshTradingEvent) {
        if (refreshEvent.isRefresh) {
            onReload()
        }
    }
}