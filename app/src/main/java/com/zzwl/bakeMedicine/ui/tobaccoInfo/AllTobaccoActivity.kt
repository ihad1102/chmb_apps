package com.zzwl.bakeMedicine.ui.tobaccoInfo

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityTobaccoAllBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.viewModel.TobaccoStatisticsViewModel

/**
 *  selectedId 被选中的烤房,
 *  type 0,1,2:运行,故障,停用
 *  isClickAble 是否可以点击
 */
@Route(path = RouterPage.AllTobaccoActivity, extras = RouteExtras.NeedOauth)
class AllTobaccoActivity : BaseActivity<ActivityTobaccoAllBinding>() {

    @JvmField
    @Autowired
    var selectedId: Int = 0

    @JvmField
    @Autowired
    var type: Int = 0

    @JvmField
    @Autowired
    var isClickAble: Boolean = true

    val isSelectedRunningObs by lazy { ObservableField(true) }
    val isSelectedFaultObs by lazy { ObservableField(false) }
    val isSelectedStopObs by lazy { ObservableField(false) }
    private val viewModel by lazy { ViewModelProviders.of(this).get(TobaccoStatisticsViewModel::class.java) }
    private val runningFragment by lazy {
        TobaccoFragment().apply {
            arguments = Bundle().apply {
                putInt("type", 1)
                putInt("selectedId", selectedId)
                putBoolean("isClickAble", isClickAble)
            }
        }
    }
    private val faultFragment by lazy {
        TobaccoFragment().apply {
            arguments = Bundle().apply {
                putInt("type", 2)
                putInt("selectedId", selectedId)
                putBoolean("isClickAble", isClickAble)
            }
        }
    }
    private val stopFragment by lazy {
        TobaccoFragment().apply {
            arguments = Bundle().apply {
                putInt("type", 0)
                putInt("selectedId", selectedId)
                putBoolean("isClickAble", isClickAble)
            }
        }
    }
    private var contentFragment: TobaccoFragment? = null
    private var isSelectedObs = isSelectedRunningObs
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.activity_tobacco_all)
        //烤房不能为0,如果不可以点击,isClickAble为false,这里设置为0,使得烤房不可能为被选中状态,
        initToolbar()
        if (!isClickAble)
            selectedId = 0
        contentView.data = this
        getData()
        onReload()
    }

    private fun initToolbar() {
        toolbar.title = "全部烤房"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initView() {
        contentFragment = runningFragment
        val transition: FragmentTransaction = supportFragmentManager.beginTransaction()
        transition.add(R.id.frameLayout, runningFragment).commit()
        when (type) {
            1 -> {
                changeSelected(isSelectedFaultObs)
                switchFragment(contentFragment!!, faultFragment)
            }
            2 -> {
                changeSelected(isSelectedStopObs)
                switchFragment(contentFragment!!, stopFragment)
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.tvRunning -> {
                changeSelected(isSelectedRunningObs)
                switchFragment(contentFragment!!, runningFragment)
            }
            R.id.tvFault -> {
                changeSelected(isSelectedFaultObs)
                switchFragment(contentFragment!!, faultFragment)
            }
            R.id.tvStop -> {
                changeSelected(isSelectedStopObs)
                switchFragment(contentFragment!!, stopFragment)
            }
        }
    }

    private fun changeSelected(tempObs: ObservableField<Boolean>) {
        isSelectedObs.set(false)
        isSelectedObs = tempObs
        isSelectedObs.set(true)
    }

    @SuppressLint("CommitTransaction")
    private fun switchFragment(fromFragment: TobaccoFragment, toFragment: TobaccoFragment) {
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

    private fun getData() {
        viewModel.getTobaccoStatistics()
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
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
}