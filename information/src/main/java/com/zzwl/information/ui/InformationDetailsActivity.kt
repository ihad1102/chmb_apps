package com.zzwl.information.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseActivity
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoActivityInformationDetailsBinding
import com.zzwl.information.router.RouterPage
import com.zzwl.information.viewModel.InformationViewModel
import com.zzwl.information.widget.UrlDrawable

@Route(path = RouterPage.InformationDetailsActivity)
class InformationDetailsActivity : BaseActivity<InfoActivityInformationDetailsBinding>() {
    @Autowired
    @JvmField
    var url: String = ""

    @Autowired
    @JvmField
    var id: Int = 0

    @Autowired
    @JvmField
    var type: Int = 0 //0,1:链接,富文本


    override var hasToolbar: Boolean = true
    var listStatus = ListStatus.Content

    private val viewModel by lazy { ViewModelProviders.of(this).get(InformationViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_activity_information_details)
        ARouter.getInstance().inject(this)
        initTitleView()
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        if (type == 0) {
            contentView.webView.visibility = View.VISIBLE
            contentView.scrollView.visibility = View.GONE
            val params = Toolbar.LayoutParams(24.dp(), 24.dp())
            params.marginStart = 0
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageResource(R.drawable.info_ic_back)
            imageView.setOnClickListener { finish() }
            imageView.layoutParams = params
            toolbar.addView(imageView)
            contentView.webView.loadUrl(url)
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
        } else {
            contentView.webView.visibility = View.GONE
            contentView.scrollView.visibility = View.VISIBLE
            getData()
        }

    }

    override fun onReload() {
        if (type == 0)
            contentView.webView.loadUrl(url)
        else
            getData()
    }

    fun getData() {
        viewModel.getInfomationDetails(id).resultNotNull().observeEx(this, {
            when (it.status) {
                Status.Loading -> {
                    if (listStatus == ListStatus.Content)
                        showLoading()
                }
                Status.Content -> {
                    contentView.textView.text = Html.fromHtml(it.data!!.content, {
                        val urlDrawable = UrlDrawable()
                        Glide.with(this)
                                .load(it)
                                .asBitmap()
                                .error(R.drawable.load_placeholder)
                                .placeholder(R.drawable.load_placeholder)
                                .into(object : SimpleTarget<Bitmap>() {
                                    private fun setDrawableToTextView(bitmap: Bitmap?) {
                                        bitmap ?: return
                                        val scale = bitmap.height / bitmap.width.toDouble()
                                        val rect = Rect(0, 0, windowManager.defaultDisplay.width, (scale * windowManager.defaultDisplay.width).toInt())
                                        val bitmapDrawable = BitmapDrawable(Bitmap.createScaledBitmap(bitmap, rect.width(), rect.height(), true))
                                        bitmapDrawable.bounds = rect
                                        urlDrawable.drawable = bitmapDrawable
                                        urlDrawable.bounds = rect
                                        contentView.textView.text = contentView.textView.text //强制再绘制一次
                                        contentView.textView.invalidate()
                                    }

                                    override fun onLoadStarted(placeholder: Drawable?) {
                                        setDrawableToTextView((placeholder as? BitmapDrawable)?.bitmap)
                                    }

                                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                                        setDrawableToTextView((errorDrawable as? BitmapDrawable)?.bitmap)
                                    }

                                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                                        setDrawableToTextView(resource)
                                    }
                                })
                        return@fromHtml urlDrawable
                    }, null)


                    showContentView()

                }
                Status.Error -> {
                    showError(it.error?.message)
                }
            }

        })
    }

    private fun initTitleView() {
        toolbar.title = "资讯详情"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        if (type == 0 && contentView.webView.canGoBack()) {
            contentView.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}