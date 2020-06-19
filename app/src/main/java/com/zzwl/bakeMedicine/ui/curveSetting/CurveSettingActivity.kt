package com.zzwl.bakeMedicine.ui.curveSetting


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.room.repository.Status
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.g.base.utils.DialogUtils
import com.g.base.utils.limitDecimal
import com.google.gson.Gson
import com.zzwl.bakeMedicine.BR
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.api.TobaccoApi
import com.zzwl.bakeMedicine.databinding.DialogCurveSettingBinding
import com.zzwl.bakeMedicine.databinding.ViewRecyclerviewBinding
import com.zzwl.bakeMedicine.room.entity.remote.CurveEntity
import com.zzwl.bakeMedicine.room.entity.remote.SelfSetting
import com.zzwl.bakeMedicine.room.entity.remote.SetCurveEntity
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.curveSetting.holder.CurveButtonHolder
import com.zzwl.bakeMedicine.ui.curveSetting.holder.CurveSettingHolder
import com.zzwl.bakeMedicine.ui.curveSetting.holder.CurveTypeHolder
import com.zzwl.bakeMedicine.viewModel.CurveSettingViewModel
import java.util.regex.Pattern


@Route(path = RouterPage.CurveSettingActivity)
class CurveSettingActivity : BaseActivity<ViewRecyclerviewBinding>() {

    @Autowired
    @JvmField
    var houseId: Int = -1

    @Autowired
    @JvmField
    var leaf: Int = 0

    val viewModel by lazy { ViewModelProviders.of(this).get(CurveSettingViewModel::class.java) }
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private val selectedObs by lazy { ObservableField(leaf) }
    private val setCurveEntity by lazy { SetCurveEntity() }
    private var justFinish = true //退出时是否弹出确认退出提示
    override var hasToolbar: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recyclerview)
        ARouter.getInstance().inject(this)
        initView()
        onReload()
    }

    private fun initView() {
        toolbar.title = "曲线设置"
        toolbar.setNavigationOnClickListener {
            if (!justFinish)
                openDialog(1)
            else
                finish()
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
    }

    private fun getData() {
        viewModel.getAllCurve(houseId)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
                            if (viewModel.curveEntityList.isNotEmpty())
                                viewModel.curveEntityList.clear()
                            if (viewModel.curveEntityListCopy.isNotEmpty())
                                viewModel.curveEntityListCopy.clear()
                            viewModel.curveEntityList.addAll(it.data!!)
                            viewModel.curveEntityList.forEach {
                                val curveEntity = CurveEntity(it.houseId, it.leafOption, arrayListOf())
                                it.selfSettings.forEach { selfSetting ->
                                    curveEntity.selfSettings.add(SelfSetting(selfSetting.dryBulbGoalTemp,
                                            selfSetting.wetBulbGoalTemp,
                                            selfSetting.tempHoldingTime,
                                            selfSetting.tempHeatingTime))
                                }
                                viewModel.curveEntityListCopy.add(curveEntity)
                            }


                            applyData(selectedObs.get()!!)
                            showContentView()
                        }
                        Status.Error -> {
                            showError(it.error?.message ?: "没有相关数据")
                        }
                    }
                })
    }

    private fun applyData(leafOption: Int) {
        if (adapter.renderItems.isNotEmpty())
            adapter.renderItems.clear()
        if (setCurveEntity.selfSettings.isNotEmpty())
            setCurveEntity.selfSettings.clear()

        val helper = LinearLayoutHelper(10.dp(), 7)
        helper.marginBottom = 10.dp()
        if (adapter.renderItems.isEmpty())
            adapter.renderItems.clear()
        setCurveEntity.houseId = houseId
        viewModel.curveEntityList[leafOption].houseId = houseId
        setCurveEntity.leafOption = leafOption.toString()
        adapter.renderItems.add(CurveTypeHolder(selectedObs).apply {
            setOnClickListener {
                val selectedLeaf = when (it.id) {
                    R.id.tvSelfCurve -> 3
                    R.id.tvUpper -> 0
                    R.id.tvMiddle -> 1
                    else -> 2
                }
                selectedObs.set(selectedLeaf)
                applyData(selectedLeaf)
            }
        })
        for (i in 0..4) {
            var temp = ""
            var temp1 = ""
            when (i) {
                0 -> {
                    temp = "一"
                    temp1 = "二"
                }
                1 -> {
                    temp = "三"
                    temp1 = "四"
                }
                2 -> {
                    temp = "五"
                    temp1 = "六"
                }
                3 -> {
                    temp = "七"
                    temp1 = "八"
                }
                4 -> {
                    temp = "九"
                    temp1 = "十"
                }
            }

            adapter.renderItems.add(CurveSettingHolder("第${temp}阶段", "第${temp1}阶段",
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2].dryBulbGoalTemp),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2 + 1].dryBulbGoalTemp),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2].wetBulbGoalTemp),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2 + 1].wetBulbGoalTemp),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2].tempHeatingTime),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2 + 1].tempHeatingTime),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2].tempHoldingTime),
                    ObservableField(viewModel.curveEntityList[leafOption].selfSettings[i * 2 + 1].tempHoldingTime)
            ).apply {
                setOnClickListener {
                    when (it.id) {
                        R.id.tvDryBulbTemp -> openSettingDialog("干球温度：", dryBulbTempObs, { dryTemp(dryBulbTempObs, leafOption, i * 2) })
                        R.id.tvWetBulbTemp -> openSettingDialog("湿球温度：", wetBulbTempObs, { wetTemp(wetBulbTempObs, leafOption, i * 2) })
                        R.id.tvHeatingTime -> {
                            if (i != 0)
                                openSettingDialog("升温时间：", heatingTimeObs, { heatTime(heatingTimeObs, leafOption, i * 2) })
                            else
                                toast("第一阶段的升温时间不允许设置")
                        }
                        R.id.tvKeepingTime -> openSettingDialog("稳温时间：", keepingTimeObs, { keepingTime(keepingTimeObs, leafOption, i * 2) })


                        R.id.tvDryBulbTemp2 -> openSettingDialog("干球温度：", dryBulbTemp2Obs, { dryTemp(dryBulbTemp2Obs, leafOption, i * 2 + 1) })
                        R.id.tvWetBulbTemp2 -> openSettingDialog("湿球温度：", wetBulbTemp2Obs, { wetTemp(wetBulbTemp2Obs, leafOption, i * 2 + 1) }, (i * 2 + 1) == 9)
                        R.id.tvHeatingTime2 -> openSettingDialog("升温时间：", heatingTime2Obs, { heatTime(heatingTime2Obs, leafOption, i * 2 + 1) })
                        R.id.tvKeepingTime2 -> openSettingDialog("稳温时间：", keepingTime2Obs, { keepingTime(keepingTime2Obs, leafOption, i * 2 + 1) })
                    }
                }
            })
            setCurveEntity.selfSettings.add(viewModel.curveEntityList[leafOption].selfSettings[i * 2])
            setCurveEntity.selfSettings.add(viewModel.curveEntityList[leafOption].selfSettings[i * 2 + 1])
        }
        adapter.renderItems.add(CurveButtonHolder().apply {
            setOnClickListener {
                when (it.id) {
                    R.id.tvReset -> {
                        openDialog(0)
                    }
                    R.id.tvConfirm -> {
                        apiProvider.create(TobaccoApi::class.java)
                                .setCurve(Gson().toJsonTree(viewModel.curveEntityList[selectedObs.get()!!]).asJsonObject)
                                .mainIoSchedulers()
                                .progressSubscribe(
                                        {
                                            justFinish = true
                                            viewModel.curveEntityListCopy[selectedObs.get()!!].selfSettings.clear()
                                            viewModel.curveEntityList[selectedObs.get()!!].selfSettings.forEach {
                                                val selfSetting = SelfSetting(
                                                        it.dryBulbGoalTemp,
                                                        it.wetBulbGoalTemp,
                                                        it.tempHoldingTime,
                                                        it.tempHeatingTime)
                                                viewModel.curveEntityListCopy[selectedObs.get()!!].selfSettings.add(selfSetting)
                                            }

                                            toast("设置曲线成功,稍后将设置到设备.")
                                        },
                                        {
                                            toast(it.message ?: "设置曲线失败")
                                        })

                    }
                }
            }
        })
        adapter.layoutHelpers = listOf(helper as LayoutHelper)
        adapter.notifyItemRangeChanged(1, 5)
    }

    private fun openDialog(type: Int) {//0,1: 重置提示,退出提示,
        val hint = if (type == 0) "是否重置曲线设置?" else "是否退出曲线设置?"
        DialogUtils.build(this, msg = hint, onPositive = {
            when (type) {
                0 -> resetCurve()
                1 -> finish()

            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun openSettingDialog(title: String, observableField: ObservableField<String>, update: () -> Boolean, isFinalStage: Boolean = false) {
        var isFirst = true
        val lastNumber = observableField.get()!!//点击取消回退到该值
        val hint = if (title.contains("湿球"))//设置提示信息
            if (isFinalStage)
                "第十阶段,湿球目标温度只能设置整数"
            else
                "如果有小数位,小数位必须为5或者0"
        else "只能设置整数"
        observableField.set("")
        val inflateDataBinding = DataBindingUtil.inflate<DialogCurveSettingBinding>(LayoutInflater.from(this), R.layout.dialog_curve_setting, null, false)

        inflateDataBinding.setVariable(BR.title, title)
        inflateDataBinding.setVariable(BR.hint, "")
        inflateDataBinding.setVariable(BR.data, observableField)

        val dialog = DialogUtils.build(this, msg = hint, view = inflateDataBinding!!.root, onPositive = {
        }, onCancel = {
            observableField.set(lastNumber)
        })
        //为了解决dialog  button 点击后自动消失,
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
            if (observableField.get()!!.trim().isNotEmpty()) {
                if (update.invoke()) {
                    justFinish = false
                    dialog.dismiss()
                }
            } else
                toast(hint)
        }
        //限制输入类型
        if (title.contains("湿球"))
            if (isFinalStage)
                inflateDataBinding.tvNumber.inputType = InputType.TYPE_CLASS_NUMBER
            else
                inflateDataBinding.tvNumber.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        else
            inflateDataBinding.tvNumber.inputType = InputType.TYPE_CLASS_NUMBER
        inflateDataBinding.tvNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isFirst) {
                    isFirst = false
                    if (s.toString().isNotEmpty())
                        inflateDataBinding.tvNumber.setSelection(s.toString().length)
                } else
                    isFirst = false

                limitDecimal(s.toString(), inflateDataBinding.tvNumber, 1)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    private fun resetCurve() {
        viewModel.curveEntityList[selectedObs.get()!!].selfSettings.clear()
        viewModel.curveEntityListCopy[selectedObs.get()!!].selfSettings.forEach { selfSetting ->
            val entity = SelfSetting(
                    selfSetting.dryBulbGoalTemp,
                    selfSetting.wetBulbGoalTemp,
                    selfSetting.tempHoldingTime,
                    selfSetting.tempHeatingTime)
            viewModel.curveEntityList[selectedObs.get()!!].selfSettings.add(entity)
        }
        applyData(selectedObs.get()!!)
    }

    override fun onReload() {
        getData()
    }

    //如果有小数位，小数位必须为5或者0 的正则
    private fun isCorrectNum(number: String): Boolean {
        val pattern = Pattern.compile("^((\\d+)|(\\d+\\.[05]))\$")
        val matcher = pattern.matcher(number)
        return matcher.matches()
    }

    //输入的温度或者时间的值判断
    private fun dryTemp(dryBulbTempObs: ObservableField<String>, leafOption: Int, i: Int): Boolean {
        val dryTemp = dryBulbTempObs.get()!!.toInt()
        return if (dryTemp in 20.0..80.0) {
            if (isCorrectNum(dryBulbTempObs.get()!!)) {
                viewModel.curveEntityList[leafOption].selfSettings[i].dryBulbGoalTemp = dryBulbTempObs.get()!!
                true
            } else {
                toast("如果有小数位，小数位必须为5或者0")
                false
            }
        } else {
            toast("干球目标温度范围： 20.0℃~80.0℃")
            false
        }
    }

    private fun wetTemp(wetBulbTempObs: ObservableField<String>, leafOption: Int, i: Int): Boolean {
        val wetTemp = wetBulbTempObs.get()!!.toFloat()
        if (wetTemp in 4.0..50.0) {
            if (isCorrectNum(wetBulbTempObs.get()!!)) {
                if (i == 10) {
                    toast("第十阶段,湿球目标温度只能设置整数")
                    return false
                } else
                    viewModel.curveEntityList[leafOption].selfSettings[i].wetBulbGoalTemp = wetBulbTempObs.get()!!
                return true
            } else {
                toast("如果有小数位，小数位必须为5或者0")
                return false
            }
        } else {
            toast("湿球目标温度范围： 4.0℃~50.0℃")
            return false
        }
    }

    private fun heatTime(heatingTimeObs: ObservableField<String>, leafOption: Int, i: Int): Boolean {

        val heatTime = heatingTimeObs.get()!!.toInt()
        return if (heatTime in 0.0..99.0) {
            if (isCorrectNum(heatingTimeObs.get()!!)) {
                viewModel.curveEntityList[leafOption].selfSettings[i].tempHeatingTime = heatingTimeObs.get()!!
                true
            } else {
                toast("如果有小数位，小数位必须为5或者0")
                false
            }
        } else {
            toast("升温时间范围： 0h~99h")
            false
        }
    }

    private fun keepingTime(keepingTimeObs: ObservableField<String>, leafOption: Int, i: Int): Boolean {
        val keepingTime = keepingTimeObs.get()!!.toInt()
        return if (keepingTime in 0.0..99.0) {
            if (isCorrectNum(keepingTimeObs.get()!!)) {
                viewModel.curveEntityList[leafOption].selfSettings[i].tempHoldingTime = keepingTimeObs.get()!!
                true
            } else {
                toast("如果有小数位，小数位必须为5或者0")
                false
            }
        } else {
            toast("稳温时间范围： 0h~99h")
            false
        }
    }

    override fun onBackPressed() {
        if (!justFinish)
            openDialog(1)
        else
            super.onBackPressed()
    }
}