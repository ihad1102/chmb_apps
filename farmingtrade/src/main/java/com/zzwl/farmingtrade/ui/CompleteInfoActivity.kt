package com.zzwl.farmingtrade.ui

import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.common.apiProvider
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.toast
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.api.SupplierApi
import com.zzwl.farmingtrade.databinding.FarmingActivityCompleteInfoBinding
import com.zzwl.farmingtrade.extend.progressOauthSubscribe
import com.zzwl.farmingtrade.router.RouterPage

/**
 * comefrom 0,1 : 从首页进入,从切换身份进入,
 */
@Route(path = RouterPage.CompleteInfoActivity, extras = RouteExtras.NeedOauth)
class CompleteInfoActivity : BaseActivity<FarmingActivityCompleteInfoBinding>() {

    override var hasToolbar: Boolean = true
    private var lastTextView: TextView? = null
    private var lastText = ""
    val isSelectedObs by lazy { ObservableField(false) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_complete_info)
        showContentView()
        contentView.data = this
        toolbar.title = "完善信息"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        contentView.tvCommit.setOnClickListener {
            if (lastText.isEmpty()) {
                toast("请选择身份，方便为您提供精准服务")
                return@setOnClickListener
            }
            apiProvider.create(SupplierApi::class.java)
                    .updateInfo(lastText)
                    .mainIoSchedulers()
                    .progressOauthSubscribe(onNext = {
                        ARouter.getInstance().build(RouterPage.SupplierHallActivity).navigation(this)
                    })
        }
    }


    fun onTextViewClick(view: View) {
        val tempView = view as TextView
        if (lastTextView !== tempView) {
            lastTextView?.setBackgroundResource(R.drawable.farming_shape_wiki_agr_dark)
            lastTextView?.setTextColor(ContextCompat.getColor(this, R.color.colorTextDarkWeek))
            tempView.setBackgroundResource(R.drawable.farming_shape_wiki_agr_accent)
            tempView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            lastTextView = tempView
            lastText = lastTextView?.text.toString()
            isSelectedObs.set(true)
        } else {
            lastText = ""
            lastTextView?.background = ContextCompat.getDrawable(this, R.drawable.farming_shape_wiki_agr_dark)
            tempView.setTextColor(ContextCompat.getColor(this, R.color.colorTextDarkWeek))
            lastTextView = null
            isSelectedObs.set(false)
        }

    }
}