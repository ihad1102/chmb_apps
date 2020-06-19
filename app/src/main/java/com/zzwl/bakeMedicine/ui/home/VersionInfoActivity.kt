package com.zzwl.bakeMedicine.ui.home

import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.g.base.help.CallPhone
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.BuildConfig
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityVersionInfoBinding
import com.zzwl.bakeMedicine.router.RouterPage

@Route(path = RouterPage.VersionInfoActivity)
class VersionInfoActivity : BaseActivity<ActivityVersionInfoBinding>() {
    override var hasToolbar: Boolean = true
    val appNameObs by lazy { ObservableField(getString(applicationInfo.labelRes)) }
    val versionObs by lazy { ObservableField(BuildConfig.VERSION_NAME) }
    val servicePhoneObs by lazy { ObservableField("029-88188685") }
    val emailObs by lazy { ObservableField("北京市宣武区广外大街9号") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_info)
        initView()
        contentView.data = this
        showContentView()
    }

    private fun initView() {
        toolbar.title = "版本信息"
        toolbar.setNavigationOnClickListener {
            finish()

        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        contentView.tvCallPhone.setOnClickListener {
            CallPhone.callTel(this@VersionInfoActivity, servicePhoneObs.get()!!)
        }
    }
}