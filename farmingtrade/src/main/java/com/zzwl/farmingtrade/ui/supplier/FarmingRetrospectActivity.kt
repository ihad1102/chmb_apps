package com.zzwl.farmingtrade.ui.supplier

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingActivityFarmingRetrospectBinding
import com.zzwl.farmingtrade.router.RouterPage

@Route(path = RouterPage.FarmingRetrospectActivity, extras = RouteExtras.NeedOauth)
class FarmingRetrospectActivity : BaseActivity<FarmingActivityFarmingRetrospectBinding>() {
    override var hasToolbar: Boolean = true
    @Autowired
    @JvmField
    var id = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.farming_activity_farming_retrospect)
        toolbar.title = "追溯详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        contentView.webView.loadUrl("http://note.tomsung.cn/?id=$id")
        contentView.webView.settings.javaScriptEnabled = true
        contentView.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                showContentView()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                showError("加载失败,请刷新重试")
                super.onReceivedError(view, request, error)
            }
        }
    }

    override fun onReload() {
        contentView.webView.loadUrl("http://note.tomsung.cn/?id=$id")
    }
}