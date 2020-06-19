package com.g.base.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.R
import com.g.base.databinding.PageBaseBinding
import com.g.base.event.ShutDownEvent
import com.g.base.extend.*
import com.g.base.help.tryCatch
import com.g.base.refWatcher
import com.g.base.room.entity.TokenEntity
import com.g.base.router.BasePage
import com.g.base.token.TokenManage
import com.g.base.ui.widget.swipeback.SlideBackManager
import com.g.base.ui.widget.swipeback.SwipeBackHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Field

/**
 * Created by G on 2017/8/8 0008.
 */

abstract class BaseActivity<SV : ViewDataBinding> : AppCompatActivity(), SlideBackManager {
    open var hasToolbar: Boolean = false
    open var isFullScreen: Boolean = false
    open var isDefaultAnim: Boolean = true
    open var isPortrait: Boolean = true
    open var isHideStatusBar: Boolean = false
    protected lateinit var contentView: SV
    private lateinit var baseBindingView: PageBaseBinding

    private lateinit var loadingDrawable: AnimationDrawable
    private lateinit var loadingLay: LinearLayout
    private lateinit var errorRefreshLay: LinearLayout
    private lateinit var needOauthLay: LinearLayout

    protected val statusBarHeight: Int by lazy {
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        //token
        //在页面创建 或者 Token改变时调用
        var isFirstGetToken = true //丢掉初始化的时候的回调
        TokenManage.getTokenLive().observeNullableEx(this) {
            if (isFirstGetToken) {
                isFirstGetToken = false
            } else {
                onTokenChange(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =if (isHideStatusBar) View.INVISIBLE else systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = systemUiVisibility
        //禁止横屏
        if (isPortrait) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        //默认的转场动画
        if (isDefaultAnim) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    @CallSuper
    override fun setContentView(layoutResID: Int) {
        //布局
        baseBindingView = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.page_base, null, false)!!
        //内容
        contentView = DataBindingUtil.inflate(layoutInflater, layoutResID, null, false)!!
        // content
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentView.root.layoutParams = params
        val mContainer = baseBindingView.container
        mContainer.addView(contentView.root, 0)
        window.setContentView(baseBindingView.root)

        //FullScreen Toolbar StatusBar的处理
        handlerWindowInset()

        loadingLay = baseBindingView.progressBar
        errorRefreshLay = baseBindingView.errorRefresh
        needOauthLay = baseBindingView.needOauthLay
        // 加载动画
        loadingDrawable = baseBindingView.imgProgress.drawable.mutate() as AnimationDrawable

        isInit = true

        //默认开始显示加载中动画
        showLoading()
        // 加载失败点击
        errorRefreshLay.setOnClickListener {
            onReload()
        }
        //去登录点击
        baseBindingView.toLogin.setOnClickListener {
            ARouter.getInstance().build(BasePage.Oauth).navigation(this)
        }

        //leakWatch
        refWatcher?.watch(this)
        //eventBus注册
        if (!EventBus.getDefault().isRegistered(this))
            tryCatch { EventBus.getDefault().register(this) }
        //token
        //在页面创建 或者 Token改变时调用
        var isFirstGetToken = true //丢掉初始化的时候的回调
        TokenManage.getTokenLive().observeNullableEx(this) {
            if (isFirstGetToken) {
                isFirstGetToken = false
            } else {
                onTokenChange(it)
            }
        }
    }

    @CallSuper
    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        fixInputMethodManagerLeak(this)
        super.onDestroy()
    }

    //////////////////////////////////////////////
    //FullScreen Toolbar StatusBar的处理
    protected open fun handlerWindowInset() {
        statusBarView.setSpace(statusBarHeight)

        //toolbar的处理
        toolbar.setMargin(statusBarHeight)
        toolbar.visibility = if (hasToolbar) {
            View.VISIBLE
        } else {
            View.GONE
        }

        //全屏的处理
        if (!isFullScreen) {
            //没有toolbar就只Margin一个StatusBar
            if (hasToolbar) {
                containerView.setMargin(statusBarHeight + resources.getDimension(R.dimen.toolbarHeight))
            } else {
                containerView.setMargin(statusBarHeight)
            }
            statusBarView.visibility = View.VISIBLE
        } else {
            containerView.setMargin(0)
            //全屏模式下StatusBar隐藏
            statusBarView.visibility = View.GONE
        }

        //StatusBar的颜色
        statusBarView.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
    }

    //失败后点击刷新点击事件
    protected open fun onReload() {}

    //tokenChange
    protected open fun onTokenChange(data: TokenEntity?) {}

    /**
     * 解决输入法中的内存泄漏问题
     */
    private fun fixInputMethodManagerLeak(context: Context?) {
        if (context == null) {
            return
        }
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val arr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
        var f: Field
        var objGet: Any?
        arr.indices
                .asSequence()
                .map { arr[it] }
                .forEach {
                    try {
                        f = imm.javaClass.getDeclaredField(it)
                        if (!f.isAccessible) {
                            f.isAccessible = true
                        }
                        objGet = f.get(imm)
                        if (objGet != null && objGet is View) {
                            val vGet = objGet as View?
                            if (vGet?.context === context) { // 被InputMethodManager持有引用的context是想要目标销毁的
                                f.set(imm, null) // 置空，破坏掉path to gc节点
                            }
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
    }

    //////////////////////////////////////////////
    //侧滑返回
    private var swipeBackHelper: SwipeBackHelper? = null

    //是否在侧滑中
    fun isSwipeSliding(): Boolean = swipeBackHelper?.isSliding() ?: false

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (swipeBackHelper == null) {
            swipeBackHelper = SwipeBackHelper(this)
        }
        return swipeBackHelper?.processTouchEvent(ev) == true || super.dispatchTouchEvent(ev)
    }

    final override fun getSlideActivity() = this

    override fun supportSlideBack() = true

    override fun canBeSlideBack() = true

    @CallSuper
    override fun finish() {
        swipeBackHelper?.finishSwipeImmediately()
        swipeBackHelper = null
        super.finish()
        if (isDefaultAnim) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    //////////////////////////////////////////////

    protected fun showLoading() {
        if (!isInit) return
        showLayout(loadingLay, true)
    }

    protected fun showContentView() {
        if (!isInit) return
        showLayout(contentView.root)
    }

    protected fun showError(tip: String? = "加载失败请点击重试", image: Int = R.drawable.err_other, clickAble: Boolean = true) {
        if (!isInit) return
        val realTip = tip ?: "加载失败请点击重试"
        errorRefreshLay.isClickable = clickAble
        baseBindingView.imgErr.setImageResource(image)
        baseBindingView.textErr.text = realTip
        showLayout(errorRefreshLay)
    }

    protected fun showEmpty(tip: String? = "还没有相关的数据", image: Int = R.drawable.err_empty, clickAble: Boolean = false) {
        if (!isInit) return
        showError(tip, image, clickAble)
        if (contentView.root.visibility != View.VISIBLE) {
            contentView.root.visibility = View.VISIBLE
        }
    }

    protected fun showNeedOauth() {
        if (!isInit) return
        setTimeout(300) { showLayout(needOauthLay) }
    }

    private fun showLayout(show: View, isLoading: Boolean = false) {
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

    override fun onBackPressed() {
        if (!childFragmentInterceptBack())
            super.onBackPressed()
    }

    private fun childFragmentInterceptBack(): Boolean =
            supportFragmentManager.fragments.any { it != null && it.isVisible && it.userVisibleHint && it is BaseFragment<*> && it.onBackPress() }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun shutDown(shutDownEvent: ShutDownEvent) {
        finish()
    }
}