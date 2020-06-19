package com.zzwl.bakeMedicine.ui.home.fragment

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import com.g.base.ui.BaseFragment
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.FragmentShopBinding

class ShopFragment : BaseFragment<FragmentShopBinding>() {
    override var hasStatusBar: Boolean = true
    override fun setContentView(): Int = R.layout.fragment_shop

    @SuppressLint("SetJavaScriptEnabled")
    override fun onLazyLoadOnce() {
        contentView.webView.loadUrl("file:///android_asset/index.html")
        contentView.webView.settings.javaScriptEnabled = true
        contentView.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                showContentView()
            }
        }
    }

    override fun onReload() {
        contentView.webView.loadUrl("file:///android_asset/index.html")
    }
}