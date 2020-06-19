package com.zzwl.farmingtrade.ui.crops

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.room.repository.Status
import com.g.base.router.BasePage
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.api.CropApi
import com.zzwl.farmingtrade.databinding.FarmingActivityCropsBinding
import com.zzwl.farmingtrade.event.*
import com.zzwl.farmingtrade.room.entity.common.CropTypeEntity
import com.zzwl.farmingtrade.router.RouterPage
import com.zzwl.farmingtrade.ui.crops.holder.CropTypeHolder
import com.zzwl.farmingtrade.viewModel.CropViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by qhn on 2018/1/8.
 *  isFollowed  ? 显示已关注:(不显示已关注的且只能选一个)
 *  isOnlyClick ? 点击作物后finish Activity 回传id
 *  amount    可以选的个数,默认为0 ,为0可以选择一个以此类推
 *  hasExtra  是否携带要显示的作物id
 */
@Route(path = RouterPage.CropsActivity, extras = RouteExtras.NeedOauth)
class CropsActivity : BaseActivity<FarmingActivityCropsBinding>() {
    val viewModel by lazy { ViewModelProviders.of(this).get(CropViewModel::class.java) }
    private var selectId: Int? = 0
    private var currentPosition = 0
    override var hasToolbar: Boolean = true
    private val observableFieldList by lazy { ArrayList<ObservableField<Int>>() }
    private var addCrops: String = ""
    private var deleteCrops: String = ""
    val addCropList by lazy { ArrayList<Int?>() }
    private val addCropNameList by lazy { ArrayList<String?>() }
    private val deleteCropList by lazy { ArrayList<Int?>() }
    private val deleteCropNameList by lazy { ArrayList<String?>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farming_activity_crops)
        showContentView()
        initView()
        requestCropType()
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        toolbar.title = "选择作物"
        toolbar.inflateMenu(R.menu.confirm_text)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
            EventBus.getDefault().post(CropEvent(""))//返回采购大厅清除选中状态
        }
        toolbar.setOnMenuItemClickListener {
            if (!intent.getBooleanExtra("isFollowed", false)) {
                if (addCropList.size == 0) {
                    toast("请选择作物!")
                    return@setOnMenuItemClickListener true
                }
                val tempId = addCropList.joinToString(",") { it.toString() }
                val tempName = addCropNameList.joinToString(",") { it.toString() }

                EventBus.getDefault().post(CropInfoEvent(tempId, tempName))
                EventBus.getDefault().post(CropIdEvent(addCropList[0], addCropNameList[0]))
                EventBus.getDefault().post(CropEvent(addCropList[0].toString()))//只在采购大厅生效
                finish()
                return@setOnMenuItemClickListener true
            }
            if (!(deleteCropList.size == 0 && addCropList.size == 0)) {
                deleteCropList.forEachIndexed { index, i ->
                    deleteCrops += i
                    if (index != deleteCropList.size - 1) {
                        deleteCrops += ","
                    }
                }
                addCropList.forEachIndexed({ index, i ->
                    addCrops += i
                    if (index != addCropList.size - 1) {
                        addCrops += ","
                    }
                })
                if (!(deleteCropList.size == 0 && addCropList.size == 0)) {
                    apiProvider.create(CropApi::class.java)
                            .collectCrops(mapOf(
                                    Pair("add", addCrops), Pair("delete", deleteCrops)
                            ))
                            .mainIoSchedulers()
                            .progressSubscribe(
                                    onNext = {
                                        //                                        手动刷新关注作物列表
                                        val addMap by lazy { HashMap<Int?, String?>() }
                                        val delMap by lazy { HashMap<Int?, String?>() }
                                        addCropList.forEachIndexed { index, it ->
                                            addMap[it] = addCropNameList[index]
                                        }
                                        deleteCropList.forEachIndexed { index, it ->
                                            delMap[it] = deleteCropNameList[index]
                                        }
                                        EventBus.getDefault().post(CropListEvent(addMap, delMap))
                                        EventBus.getDefault().post(RefreshEvent(true))
                                        finish()
                                    },
                                    showError = {
                                        if (!TokenManage.isOauth()) {
                                            ARouter.getInstance().build(BasePage.Oauth).navigation(this)
                                        } else {
                                            addCropList.clear()
                                            deleteCropList.clear()
                                            addCrops = ""
                                            deleteCrops = ""
                                            EventBus.getDefault().post(CropReloadEvent(true))
                                        }
                                        it.dismiss()
                                    }
                            )
                } else toast("请选择作物!")
            } else toast("请选择作物!")
            return@setOnMenuItemClickListener true
        }
    }


    private fun requestCropType() {
        viewModel.getCropType().resultNotNull().observeExOnce(this, {
            when (it.status) {
                Status.Loading -> {
                    showLoading()
                }
                Status.Content -> {
                    showContentView()
                    bindRecycleView(it.data!!)
                }
                Status.Error -> {
                    showError(it.error?.message)
                }
            }

        })
    }

    private fun bindRecycleView(list: List<CropTypeEntity>) {
        val fragmentList = ArrayList<CropFragment>()
        val adapter = setupRecyclerView(contentView.recyclerView)
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = list.size
        linearLayoutHelper.setDividerHeight(2)
        adapter.layoutHelpers = arrayListOf(linearLayoutHelper as LayoutHelper)
        list.forEachIndexed { index, cropTypeEntity ->
            fragmentList.add(CropFragment()
                    .apply {
                        arguments = Bundle().apply {
                            putInt("categoryId", list[index].id!!)
                            putInt("collectCount", list[index].collectCount ?: 0)
                            putInt("amount", intent.getIntExtra("amount", 0))
                            putBoolean("isFollowed", intent.getBooleanExtra("isFollowed", false))
                            putBoolean("isOnlyClick", intent.getBooleanExtra("isOnlyClick", false))
                        }
                    })
            val obsIsSelected = if (index == 0) {
                //第一次进入activity 加载第一个fragment
                val transaction = supportFragmentManager.beginTransaction()
                if (!fragmentList[0].isAdded) {
                    transaction.add(R.id.flCrops, fragmentList[0])
                }
                transaction.show(fragmentList[0])
                transaction.commit()
                ObservableField(R.color.colorPageBg)
            } else ObservableField(R.color.colorTextWhite)
            observableFieldList.add(obsIsSelected)
            adapter.renderItems.add(CropTypeHolder(cropTypeEntity, index, obsIsSelected)
                    .apply {
                        setOnClickListener {
                            selectId = cropTypeEntity.id
                            observableFieldList[currentPosition].set(R.color.colorTextWhite)
                            //被选中的holder
                            observableFieldList[position].set(R.color.colorPageBg)
                            changeFragment(position, fragmentList)
                        }
                    })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(selectedCropsEvent: SelectedCropsEvent) {
        selectCrop(selectedCropsEvent)
    }

    //选择作物,
    private fun selectCrop(selectedCropsEvent: SelectedCropsEvent) {
        if (selectedCropsEvent.type == 1) {
            //作物id
            if (!addCropList.contains(selectedCropsEvent.cropId)) {
                if (!deleteCropList.contains(selectedCropsEvent.cropId))
                    addCropList.add(selectedCropsEvent.cropId)
                else
                    deleteCropList.remove(selectedCropsEvent.cropId)
            }
            //作物名
            if (!addCropNameList.contains(selectedCropsEvent.cropName)) {
                if (!deleteCropNameList.contains(selectedCropsEvent.cropName))
                    addCropNameList.add(selectedCropsEvent.cropName)
                else
                    deleteCropList.remove(selectedCropsEvent.cropId)
            }
        } else if (selectedCropsEvent.type == 0) {
            if (addCropList.contains(selectedCropsEvent.cropId)) {
                addCropList.remove(selectedCropsEvent.cropId)
            } else if (!deleteCropList.contains(selectedCropsEvent.cropId)) {
                deleteCropList.add(selectedCropsEvent.cropId)
            }
            if (addCropNameList.contains(selectedCropsEvent.cropName)) {
                addCropNameList.remove(selectedCropsEvent.cropName)
            } else if (!deleteCropNameList.contains(selectedCropsEvent.cropName)) {
                deleteCropNameList.add(selectedCropsEvent.cropName)
            }
        }
    }

    private fun changeFragment(index: Int, fragmentList: ArrayList<CropFragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        currentPosition = index
        transaction.replace(R.id.flCrops, fragmentList[index])
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().post(CropEvent(""))
    }
}