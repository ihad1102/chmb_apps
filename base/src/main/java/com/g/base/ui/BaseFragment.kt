package com.g.base.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.R
import com.g.base.databinding.PageBaseBinding
import com.g.base.extend.*
import com.g.base.help.tryCatch
import com.g.base.room.entity.TokenEntity
import com.g.base.router.BasePage
import com.g.base.token.TokenManage
import org.greenrobot.eventbus.EventBus

/**
 * Created by G on 2017/8/8 0008.
 */
abstract class BaseFragment<SV : ViewDataBinding> : Fragment() {
    protected open var hasToolbar: Boolean = false
    protected open var hasStatusBar: Boolean = false
    protected open var isFullScreen: Boolean = false

    protected lateinit var contentView: SV
    private lateinit var baseBindingView: PageBaseBinding

    private lateinit var loadingDrawable: AnimationDrawable
    private lateinit var loadingLay: LinearLayout
    private lateinit var errorRefreshLay: LinearLayout
    private lateinit var needOauthLay: LinearLayout

    val statusBarHeight: Int by lazy {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            24.dp()
        }
    }
    val toolbar: Toolbar
        get() = baseBindingView.toolbar?.toolbar as Toolbar
    val baseRootView: FrameLayout
        get() = baseBindingView.rootLayout
    val containerView: FrameLayout
        get() = baseBindingView.container
    val statusBarView: View
        get() = baseBindingView.statusBarMock

    var isInit = false
    var isLoad = false

    abstract fun setContentView(): Int

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as? BaseActivity<*>) ?: throw RuntimeException("BaseFragment 的父Activity必须为BaseActivity")
        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        //布局
        baseBindingView = DataBindingUtil.inflate(inflater, R.layout.page_base, null, false)!!
        //内容
        contentView = DataBindingUtil.inflate(inflater, setContentView(), null, false)!!
        // content
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentView.root.layoutParams = params
        val mContainer = baseBindingView.container
        mContainer.addView(contentView.root, 0)

        //FullScreen Toolbar StatusBar的处理
        handlerWindowInset()

        loadingLay = baseBindingView.progressBar
        errorRefreshLay = baseBindingView.errorRefresh
        needOauthLay = baseBindingView.needOauthLay
        // 加载动画
        loadingDrawable = baseBindingView.imgProgress.drawable.mutate() as AnimationDrawable
        // 加载失败点击
        errorRefreshLay.setOnClickListener {
            onReload()
        }
        //去登录点击
        baseBindingView.toLogin.setOnClickListener {
            ARouter.getInstance().build(BasePage.Oauth).navigation(context)
        }
        return baseBindingView.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //eventBus注册
        if (!EventBus.getDefault().isRegistered(this))
            tryCatch { EventBus.getDefault().register(this) }
        //在页面创建 或者 Token改变时调用
        var isFirstGetToken = true //丢掉初始化的时候的回调
        TokenManage.getTokenLive().observeNullableEx(this) {
            if (isFirstGetToken) {
                isFirstGetToken = false
            } else {
                onTokenChange(it)
            }
        }
        isInit = true
        showLoading()
        isCanLoadData()
    }

    override fun onDestroyView() {
        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        isInit = false
        isLoad = false
        super.onDestroyView()
    }

    ////////////////////////////////////////////
    //FullScreen Toolbar StatusBar的处理
    protected open fun handlerWindowInset() {
        val fragmentStatusBarHeight = if (hasStatusBar) statusBarHeight else 0
        //statusBar高度处理
        statusBarView.setSpace(fragmentStatusBarHeight)
        //toolbar的处理
        toolbar.setMargin(fragmentStatusBarHeight)
        toolbar.visibility = if (hasToolbar) {
            View.VISIBLE
        } else {
            View.GONE
        }
        //全屏的处理
        if (!isFullScreen) {
            //没有toolbar就只Margin一个marginStatusBarHeight
            if (hasToolbar) {
                containerView.setMargin(fragmentStatusBarHeight + resources.getDimension(R.dimen.toolbarHeight))
            } else {
                containerView.setMargin(fragmentStatusBarHeight)
            }
            statusBarView.visibility = View.VISIBLE
        } else {
            containerView.setMargin(0)
            //全屏模式下StatusBar透明
            statusBarView.visibility = View.GONE
        }

        //StatusBar的颜色
        statusBarView.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
    }

    //失败后点击刷新
    protected open fun onReload() {}

    //tokenChange
    protected open fun onTokenChange(data: TokenEntity?) {}

    fun showLoading() {
        if (!isInit) return
        showOrHideView(loadingLay, true)
    }

    fun showContentView() {
        if (!isInit) return
        showOrHideView(contentView.root)
    }

    fun showError(tip: String? = "加载失败请点击重试", image: Int = R.drawable.err_other, clickAble: Boolean = true) {
        if (!isInit) return
        val realTip = tip ?: "加载失败请点击重试"
        errorRefreshLay.isClickable = clickAble
        baseBindingView.imgErr.setImageResource(image)
        baseBindingView.textErr.text = realTip
        showOrHideView(errorRefreshLay)
    }

    fun showEmpty(tip: String? = "还没有相关的数据", image: Int = R.drawable.err_empty, clickAble: Boolean = false) {
        if (!isInit) return
        showError(tip, image, clickAble)
        if (contentView.root.visibility != View.VISIBLE) {
            contentView.root.visibility = View.VISIBLE
        }
    }

    fun showNeedOauth() {
        if (!isInit) return
        setTimeout( 300L){ showOrHideView(needOauthLay) }
    }

    private fun showOrHideView(show: View, isLoading: Boolean = false) {
        if (!isInit) return
        if (show != loadingLay && loadingLay.visibility != View.GONE) {
            loadingLay.visibility = View.GONE
        }
        if (show != contentView.root && contentView.root.visibility != View.GONE) {
            contentView.root.visibility = View.GONE
        }
        if (show != errorRefreshLay && errorRefreshLay.visibility != View.GONE) {
            errorRefreshLay.visibility = View.GONE
        }
        if (show != needOauthLay && needOauthLay.visibility != View.GONE) {
            needOauthLay.visibility = View.GONE
        }

        if (show.visibility != View.VISIBLE) {
            show.visibility = View.VISIBLE
        }

        // 加载动画
        if (isLoading) {
            loadingDrawable.start()
        } else {
            loadingDrawable.stop()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isCanLoadData()
    }

    open fun onLazyLoad() {}
    open fun onLazyLoadOnce() {}
    open fun onStopLoad() {}

    protected open fun isCanLoadData() {
        if (!isInit) {
            return
        }
        if (userVisibleHint) {
            val oldIsLoad = isLoad
            isLoad = true
            if (!oldIsLoad) {
                onLazyLoadOnce()
            }
            onLazyLoad()
        } else {
            if (isLoad) {
                onStopLoad()
            }
        }
    }

    open fun onBackPress(): Boolean = false
}