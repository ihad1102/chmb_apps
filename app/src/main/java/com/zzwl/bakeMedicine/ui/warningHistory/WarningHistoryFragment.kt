package com.zzwl.bakeMedicine.ui.warningHistory

import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.view.LayoutInflater
import android.widget.DatePicker
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.api.ErrorCode
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseFragment
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.DialogDatepickerBinding
import com.zzwl.bakeMedicine.databinding.FragmentWarningBinding
import com.zzwl.bakeMedicine.event.WarningEvent
import com.zzwl.bakeMedicine.ui.warningHistory.holder.TitleHolder
import com.zzwl.bakeMedicine.ui.warningHistory.holder.WarningHolder
import com.zzwl.bakeMedicine.viewModel.WarningViewModel
import java.util.*

class WarningHistoryFragment : BaseFragment<FragmentWarningBinding>() {
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView2) }
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(WarningViewModel::class.java) }
    val beginTimeObs by lazy { ObservableField("请选择日期") }
    val endTimeObs by lazy { ObservableField("请选择日期") }
    private var listStatus = ListStatus.Content
    var isCurrentFragment = true
    private var keyWorld: String? = null
    private var houseId: String? = null
    private var isSetDateFrom = false
    private var isSetDateTo = false
    private val calendar by lazy { Calendar.getInstance() }
    private var yearFromDate = calendar.get(Calendar.YEAR)
    private var monthFromDate = calendar.get(Calendar.MONTH) + 1
    private var dayFromDate = calendar.get(Calendar.DAY_OF_MONTH)

    private var yearToDate = calendar.get(Calendar.YEAR)
    private var monthToDate = calendar.get(Calendar.MONTH) + 1
    private var dayToDate = calendar.get(Calendar.DAY_OF_MONTH)
    private var groupId = ""
    override fun setContentView(): Int = R.layout.fragment_warning
    override fun onLazyLoadOnce() {
        contentView.data = this@WarningHistoryFragment
        initView()
        groupId = context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1).toString()
        setDataObs()
        onReload()
    }

    private fun initView() {
        isCurrentFragment = arguments!!.getInt("type", 0) == 0
        contentView.refreshLayout.setOnRefreshListener {
            if (listStatus == ListStatus.Content) {
                listStatus = ListStatus.Refreshing
                onReload()
            } else {
                toast("正在执行其他操作请稍后再试")
            }
        }
        setLoadingListener()
        contentView.tvFromDate.setOnClickListener {
            openDatePickerDialog(0, yearFromDate, monthFromDate, dayFromDate)
        }
        contentView.tvToDate.setOnClickListener {
            if (isSetDateFrom)
                openDatePickerDialog(1, yearToDate, monthToDate, dayToDate)
            else
                toast("请先选择开始时间")

        }
    }

    private fun setLoadingListener() {
        adapter.setOnLoadingListener {
            if (listStatus == ListStatus.Content) {
                val limit = viewModel.limit
                val pageNum = if (isCurrentFragment) viewModel.currentWarningList.size / limit + 1 else viewModel.historyWarningList.size / limit + 1
                if (isCurrentFragment)
                    viewModel.setCurrentWarning(houseId, pageNum, keyWorld, groupId)
                else {
                    val tempBeginTime = if (beginTimeObs.get()!! == "请选择日期") "" else beginTimeObs.get()!! + " 00:00:00"
                    val tempEndTime = if (endTimeObs.get()!! == "请选择日期") "" else endTimeObs.get()!! + " 23:59:59"
                    viewModel.setHistoryWarning(houseId, pageNum, tempBeginTime, tempEndTime, groupId, keyWorld)
                }
                listStatus = ListStatus.LoadMore
            } else {
                adapter.setLoadingFailed()
                toast("正在执行其他操作请稍后再试")
            }

        }
    }

    private fun setDataObs() {
        when (arguments!!.getInt("type", 0)) {
            0 -> {
                viewModel.obsCurrentWarning()
                        .resultNotNull()
                        .observeEx(this, {
                            when (it.status) {
                                Status.Loading -> {
                                    if (listStatus == ListStatus.Content)
                                        showLoading()
                                }
                                Status.Content -> {
                                    if (listStatus == ListStatus.LoadMore) {
                                        viewModel.currentWarningList.addAll(it.data!!.list)
                                    } else {
                                        viewModel.currentWarningList.clear()
                                        viewModel.currentWarningList.addAll(it.data!!.list)
                                        if (contentView.refreshLayout.isRefreshing)
                                            contentView.refreshLayout.isRefreshing = false
                                    }
                                    if (it.data!!.list.size < viewModel.limit)
                                        adapter.setLoadingNoMore()
                                    else
                                        adapter.setLoadingSucceed()
                                    applyCurrentData()
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
                                            showError(it.error?.message)
                                        }
                                        ListStatus.Refreshing -> {
                                            if (contentView.refreshLayout.isRefreshing) {
                                                contentView.refreshLayout.isRefreshing = false
                                            }
                                            showError(it.error?.message)
                                        }
                                    }
                                    listStatus = ListStatus.Content
                                }
                            }
                        })
            }
            1 -> {
                viewModel.obsHistoryWarning()
                        .resultNotNull()
                        .observeEx(this, {
                            when (it.status) {
                                Status.Loading -> {
                                    if (listStatus == ListStatus.Content)
                                        showLoading()
                                }
                                Status.Content -> {
                                    if (listStatus == ListStatus.LoadMore) {
                                        viewModel.historyWarningList.addAll(it.data!!.list)
                                    } else {
                                        viewModel.historyWarningList.clear()
                                        viewModel.historyWarningList.addAll(it.data!!.list)
                                        if (contentView.refreshLayout.isRefreshing)
                                            contentView.refreshLayout.isRefreshing = false
                                    }
                                    if (it.data!!.list.size < viewModel.limit)
                                        adapter.setLoadingNoMore()
                                    else
                                        adapter.setLoadingSucceed()
                                    applyHistoryData()
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
                                        ListStatus.Content, ListStatus.Refreshing -> {
                                            if (contentView.refreshLayout.isRefreshing) {
                                                contentView.refreshLayout.isRefreshing = false
                                            }
                                            viewModel.historyWarningList.clear()
                                            applyHistoryData()
                                            adapter.removeOnLoadingListener()
                                            showEmpty(it.error?.message)
                                        }
                                    }
                                    listStatus = ListStatus.Content
                                }
                            }

                        })
            }
        }

    }

    private fun applyCurrentData() {
        if (adapter.renderItems.isNotEmpty())
            adapter.renderItems.clear()
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.currentWarningList.size + 2
        adapter.renderItems.add(TitleHolder(isCurrentFragment))
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.renderItems.add(WarningHolder(null, true, isCurrentFragment, "告警名称", "级别", "烤房名称", "发生时间", "恢复时间", false))
        viewModel.currentWarningList.forEachIndexed { index, it ->
            val tempLevel = when (it.level) {
                0 -> "一般"

                1 -> "重要"

                2 -> "严重"

                else -> "严重"

            }
            adapter.renderItems.add(WarningHolder(it.houseId, false, isCurrentFragment, it.alarmName, tempLevel, it.houseName, it.createTime, it.updateTime, index % 2 == 0))
        }
        adapter.notifyDataSetChanged()
    }

    private fun applyHistoryData() {
        if (adapter.renderItems.isNotEmpty())
            adapter.renderItems.clear()
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = viewModel.historyWarningList.size + 2
        adapter.renderItems.add(TitleHolder(isCurrentFragment))
        adapter.renderItems.add(WarningHolder(null, true, isCurrentFragment, "告警名称", "级别", "烤房名称", "发生时间", "恢复时间", false))
        viewModel.historyWarningList.forEachIndexed { index, it ->
            val tempLevel = when (it.level) {
                0 -> "一般"

                1 -> "重要"

                else -> "严重"

            }
            adapter.renderItems.add(WarningHolder(it.houseId, false, isCurrentFragment, it.alarmName, tempLevel, it.houseName, it.createTime, it.updateTime, index % 2 == 0))
        }
        adapter.notifyDataSetChanged()
    }

    override fun onReload() {
        when (arguments!!.getInt("type", 0)) {
            0 -> {
                viewModel.setCurrentWarning(houseId, 1, keyWorld, groupId)
            }
            1 -> {
                setLoadingListener()
                val tempBeginTime = if (beginTimeObs.get()!! == "请选择日期") "" else beginTimeObs.get()!! + " 00:00:00"
                val tempEndTime = if (endTimeObs.get()!! == "请选择日期") "" else endTimeObs.get()!! + " 23:59:59"
                viewModel.setHistoryWarning(houseId, 1, tempBeginTime, tempEndTime, groupId, keyWorld)
            }
        }

    }

    fun refreshFragment(data: WarningEvent) {
        keyWorld = data.keyword
        houseId = data.houseId
        if (arguments!!.getInt("type", 0) == 0) {
            viewModel.setCurrentWarning(houseId, 1, keyWorld, groupId)
        } else {
            setLoadingListener()
            val tempBeginTime = if (beginTimeObs.get()!! == "请选择日期") "" else beginTimeObs.get()!! + " 00:00:00"
            val tempEndTime = if (endTimeObs.get()!! == "请选择日期") "" else endTimeObs.get()!! + " 23:59:59"
            viewModel.setHistoryWarning(houseId, 1, tempBeginTime, tempEndTime, groupId, keyWorld)
        }
    }

    /**
     *  0: dateFrom,
     *  1: dateTo
     */
    private fun openDatePickerDialog(type: Int, year: Int, month: Int, day: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("设置") { dialog, _ ->
            if (type == 0) {
                beginTimeObs.set("$yearFromDate-$monthFromDate-$dayFromDate")
                isSetDateFrom = true
                if (isSetDateTo)
                    onReload()
            } else {
                isSetDateTo = true
                endTimeObs.set("$yearToDate-$monthToDate-$dayToDate")
                onReload()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("取消", { dialog, a ->
            if (type == 0) {
                isSetDateFrom = false
                beginTimeObs.set("请选择日期")
            } else {
                isSetDateTo = false
                endTimeObs.set("请选择日期")
            }
            dialog.dismiss()
        })
        val dialog = builder.create()
        val inflateDataBinding = DataBindingUtil.inflate<DialogDatepickerBinding>(LayoutInflater.from(context), R.layout.dialog_datepicker, null, false)
        dialog.setView(inflateDataBinding.root)

        inflateDataBinding.datePicker.init(year, month - 1, day) { _, years, monthOfYear, dayOfMonth ->
            if (type == 0) {
                yearFromDate = years
                monthFromDate = monthOfYear + 1
                dayFromDate = dayOfMonth
            } else {
                yearToDate = years
                monthToDate = monthOfYear + 1
                dayToDate = dayOfMonth
            }
        }
        dialog.show()
    }

}