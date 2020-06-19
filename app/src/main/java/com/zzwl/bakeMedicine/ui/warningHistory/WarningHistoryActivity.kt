package com.zzwl.bakeMedicine.ui.warningHistory

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.extend.dp
import com.g.base.extend.inhibitingInput
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.g.base.utils.PopupWindowUtils
import com.zzwl.bakeMedicine.BR
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityWarningBinding
import com.zzwl.bakeMedicine.databinding.ViewPopwindowTobaccoBinding
import com.zzwl.bakeMedicine.event.WarningEvent
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.warningHistory.holder.TobaccoHolder
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel

/**
 *  selectedType: 0,1 : 当前告警,历史告警
 */
@Route(path = RouterPage.WarningHistoryActivity, extras = RouteExtras.NeedOauth)
class WarningHistoryActivity : BaseActivity<ActivityWarningBinding>() {
    val selectedTypeObs by lazy { ObservableField(0) }//被选中的fragment
    private val selectedIdObs by lazy { ObservableField<Int?>() }//被选中的烤房id
    val selectedNameObs by lazy { ObservableField("全部") }//被选中的烤房name
    private var currentHouseId: Int? = null
    private var historyHouseId: Int? = null
    private var currentHouseName = "全部"
    private var historyHouseName = "全部"
    private val viewModel by lazy { ViewModelProviders.of(this).get(TobaccoStatisticsViewModel::class.java) }
    private var contentFragment: WarningHistoryFragment? = null
    override var hasToolbar: Boolean = true

    private val currentFragment by lazy {
        WarningHistoryFragment().apply {
            arguments = Bundle().apply {
                putInt("type", 0)
            }
        }
    }
    private val historyFragment by lazy {
        WarningHistoryFragment().apply {
            arguments = Bundle().apply {
                putInt("type", 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warning)
        getData()
        onReload()
    }

    private fun getData() {
        viewModel.getTobaccoStatistics()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
                            contentView.data = this
                            viewModel.tobaccoListInfoEntity = it.data!!
                            initView()
                            showContentView()
                        }
                        Status.Error -> {
                            showError(it.error?.message)
                        }
                    }

                })
    }

    private fun initView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.title = "烤房告警"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.edtSearch.setHintTextColor(ContextCompat.getColor(this, R.color.colorHintSearch))
        contentView.tvCurrent.setOnClickListener {
            selectedTypeObs.set(0)
            selectedIdObs.set(currentHouseId)
            selectedNameObs.set(currentHouseName)
            switchFragment(contentFragment!!, currentFragment)

        }
        contentView.tvHistory.setOnClickListener {
            selectedTypeObs.set(1)
            selectedIdObs.set(historyHouseId)
            selectedNameObs.set(historyHouseName)
            switchFragment(contentFragment!!, historyFragment)
        }

        contentFragment = currentFragment
        val transition: FragmentTransaction = supportFragmentManager.beginTransaction()
        transition.add(R.id.frameLayout, contentFragment).commit()

        contentView.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                postEvent()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        contentView.viewClick.setOnClickListener {
            openPopWindow()
        }
        inhibitingInput(contentView.edtSearch)
    }

    private var inflateDataBinding: ViewPopwindowTobaccoBinding? = null
    private lateinit var popupWindow: PopupWindow
    private fun openPopWindow() {
        inflateDataBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.view_popwindow_tobacco, null, false)
        inflateDataBinding!!.setVariable(BR.data, this)
        popupWindow = PopupWindowUtils.build(inflateDataBinding!!.root,
                contentView.imgTobaccoList,
                88.dp(),
                180.dp(),
                8.dp(),
                16,
                Gravity.RIGHT)
        val adapter = setupRecyclerView(inflateDataBinding!!.recyclerView)
        adapter.renderItems.add(TobaccoHolder(" 全部 ", null, selectedIdObs).apply {
            setOnClickListener {
                onClick()
            }
        })
        viewModel.tobaccoListInfoEntity.houseInfoList.forEach {
            adapter.renderItems.add(TobaccoHolder(it.name, it.houseId, selectedIdObs).apply {
                setOnClickListener {
                    onClick()
                }
            })
        }
        adapter.notifyDataSetChanged()
    }

    private fun TobaccoHolder.onClick() {
        if (selectedTypeObs.get()!! == 0) {
            currentHouseId = houseId
            currentHouseName = tobaccoName
        } else {
            historyHouseId = houseId
            historyHouseName = tobaccoName
        }
        selectedIdObs.set(houseId)
        selectedNameObs.set(tobaccoName)
        popupWindow.dismiss()
        postEvent()
    }


    @SuppressLint("CommitTransaction")
    private fun switchFragment(fromFragment: WarningHistoryFragment, toFragment: WarningHistoryFragment) {
        if (contentFragment != toFragment) {
            contentFragment = toFragment
            val transition: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (!toFragment.isAdded) {
                transition.hide(fromFragment).add(R.id.frameLayout, toFragment).commit()
            } else {
                transition.hide(fromFragment).show(toFragment).commit()
            }
        }
    }

    override fun onReload() {
        viewModel.setGroupLiveData(getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", -1))
    }

    private fun postEvent() {
        if (selectedTypeObs.get()!! == 0) {
            currentFragment.refreshFragment(WarningEvent(selectedTypeObs.get()!!, contentView.edtSearch.text.toString(), selectedIdObs.get()?.toString()
                    ?: ""))
        } else {
            historyFragment.refreshFragment(WarningEvent(selectedTypeObs.get()!!, contentView.edtSearch.text.toString(), selectedIdObs.get()?.toString()
                    ?: ""))
        }

    }


    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode: Int = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else View.MeasureSpec.EXACTLY
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }
}