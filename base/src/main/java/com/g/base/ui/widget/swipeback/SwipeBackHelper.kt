package com.g.base.ui.widget.swipeback

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.g.base.ActivityLifecycleHelper

open class SwipeBackHelper(private val slideBackManager: SlideBackManager) {
    companion object {
        private val SHADOW_WIDTH = 64 //px 阴影宽度
        private val EDGE_SIZE = 50  //dp 默认拦截手势区间
    }

    private var mIsSliding: Boolean = false //是否正在滑动
    private var mIsSlideAnimPlaying: Boolean = false //滑动动画展示过程中
    private var mDistanceX: Float = 0.toFloat()  //px 当前滑动距离 （正数或0）
    private var mLastPointX: Float = 0.toFloat()  //记录手势在屏幕上的X轴坐标


    //滑动拦截事件的区域
    private val mEdgeSize: Int  by lazy {
        val density = mDisplayMetrics.density
        return@lazy (EDGE_SIZE * density + 0.5f).toInt()
    }
    private val mTouchSlop by lazy { ViewConfiguration.get(mActivity).scaledTouchSlop }
    private val mActivity: Activity  by lazy { slideBackManager.getSlideActivity() }
    private val mDisplayMetrics by lazy { mActivity.resources.displayMetrics }
    private val mViewManager: ViewManager by lazy { ViewManager() }
    private val mCurrentContentLayout: FrameLayout by lazy { getContentView(mActivity)!! }

    private var mAnimatorSet: AnimatorSet? = null
    private var mIsInThresholdArea: Boolean = false

    fun finishSwipeImmediately() {
        if (mIsSliding) {
            mViewManager.clean(false)
        }
        mAnimatorSet?.cancel()
    }

    fun isSliding() = mIsSliding

    fun processTouchEvent(ev: MotionEvent): Boolean {
        //不支持滑动返回，则手势事件交给View处理
        if (!slideBackManager.supportSlideBack()) {
            return false
        }

        //正在滑动动画播放中，直接消费手势事件
        if (mIsSlideAnimPlaying) {
            return true
        }

        val action = ev.action and MotionEvent.ACTION_MASK
        if (action == MotionEvent.ACTION_DOWN) {
            mLastPointX = ev.rawX
            mIsInThresholdArea = mLastPointX >= 0 && mLastPointX <= mEdgeSize
        }

        //不满足滑动区域，不做处理
        if (!mIsInThresholdArea) {
            return false
        }

        val actionIndex = ev.actionIndex
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN ->
                //有第二个手势事件加入，而且正在滑动事件中，则直接消费事件
                if (mIsSliding) {
                    return true
                }
            MotionEvent.ACTION_MOVE -> {
                //一旦触发滑动机制，拦截所有其他手指的滑动事件
                if (actionIndex != 0) {
                    return mIsSliding
                }
                val curPointX = ev.rawX
                val isSliding = mIsSliding
                if (!isSliding) {
                    if (Math.abs(curPointX - mLastPointX) < mTouchSlop) { //判断是否满足滑动
                        return false
                    } else {
                        mIsSliding = true
                    }
                }
                mViewManager.setup()
                onSliding(curPointX)
                if (mIsSliding && isSliding != mIsSliding){
                    ev.action = MotionEvent.ACTION_CANCEL
                }
                return isSliding == mIsSliding
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_OUTSIDE -> {
                //没有进行滑动
                if (mDistanceX <= 0f && actionIndex == 0) {
                    mIsSliding = false
                    mViewManager.clean()
                    return false
                }
                // 取消滑动 或 手势抬起 ，而且手势事件是第一手势，开始滑动动画
                if (mIsSliding && actionIndex == 0) {
                    mIsSliding = false
                    val width = mDisplayMetrics.widthPixels
                    if (mDistanceX > width / 4)
                        startSlideAnim(false)
                    else
                        startSlideAnim(true)
                    return true
                } else if (mIsSliding) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 手动处理滑动事件
     */
    private fun onSliding(curPointX: Float) {
        val previewActivityContentView = mViewManager.mPreviousContentView
        if (previewActivityContentView == null) {
            mDistanceX = 0f
            mIsSliding = false
            mViewManager.clean()
            return
        }

        val currentContentView = mViewManager.mNowContentView
        val shadowView = mViewManager.mShadowView
        val width = mDisplayMetrics.widthPixels
        val distanceX = curPointX - mLastPointX
        mLastPointX = curPointX
        mDistanceX += distanceX
        if (mDistanceX < 0) {
            mDistanceX = 0f
        }

        previewActivityContentView.x = -width / 3 + mDistanceX / 3
        currentContentView?.x = mDistanceX
        shadowView?.x = mDistanceX - SHADOW_WIDTH
    }

    /**
     * 开始自动滑动动画
     *
     * @param slideCanceled 是不是要返回（true则不关闭当前页面）
     */
    private fun startSlideAnim(slideCanceled: Boolean) {
        val previewView = mViewManager.mPreviousContentView ?: return
        val currentView = mViewManager.mNowContentView ?: return
        val shadowView = mViewManager.mShadowView

        val width = mDisplayMetrics.widthPixels
        val interpolator = DecelerateInterpolator(2f)

        // preview activity's animation
        val previewViewAnim = ObjectAnimator()
        previewViewAnim.interpolator = interpolator
        previewViewAnim.setProperty(View.TRANSLATION_X)
        val preViewStart = mDistanceX / 3 - width / 3
        val preViewStop = (if (slideCanceled) -width / 3 else 0).toFloat()
        previewViewAnim.setFloatValues(preViewStart, preViewStop)
        previewViewAnim.target = previewView

        // shadow view's animation
        val shadowViewAnim = ObjectAnimator()
        shadowViewAnim.interpolator = interpolator
        shadowViewAnim.setProperty(View.TRANSLATION_X)
        val shadowViewStart = mDistanceX - SHADOW_WIDTH
        val shadowViewEnd = ((if (slideCanceled) 0 else width) - SHADOW_WIDTH).toFloat()
        shadowViewAnim.setFloatValues(shadowViewStart, shadowViewEnd)
        shadowViewAnim.target = shadowView

        // current view's animation
        val currentViewAnim = ObjectAnimator()
        currentViewAnim.interpolator = interpolator
        currentViewAnim.setProperty(View.TRANSLATION_X)
        val curViewStart = mDistanceX
        val curViewStop = (if (slideCanceled) 0 else width).toFloat()
        currentViewAnim.setFloatValues(curViewStart, curViewStop)
        currentViewAnim.target = currentView

        // play animation together
        mAnimatorSet = AnimatorSet()
        mAnimatorSet?.duration = (if (slideCanceled) 150 else 300).toLong()
        mAnimatorSet?.playTogether(previewViewAnim, shadowViewAnim, currentViewAnim)
        mAnimatorSet?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mAnimatorSet?.removeListener(this)
                mViewManager.clean(slideCanceled)
                if (slideCanceled) {
                    mIsSlideAnimPlaying = false
                    currentView.x = 0f
                    mDistanceX = 0f
                    mIsSliding = false
                } else {
                    mActivity.finish()
                    mActivity.overridePendingTransition(0, 0)
                }
            }
        })
        mAnimatorSet?.start()
        mIsSlideAnimPlaying = true
    }


    private fun getContentView(activity: Activity?): FrameLayout? =
            activity?.findViewById<View>(Window.ID_ANDROID_CONTENT) as? FrameLayout

    internal inner class ViewManager {
        var mPreviousContentView: View? = null
        var mNowContentView: View? = null
        var mShadowView: View? = null
        private var hasSetup = false

        fun setup() {
            if (hasSetup) return
            hasSetup = true
            val inputMethod = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = mActivity.currentFocus
            if (view != null) {
                inputMethod.hideSoftInputFromWindow(view.windowToken, 0)
            }
            mNowContentView = mCurrentContentLayout.getChildAt(0)
            addPreView()
            addShadowView()
        }

        fun clean(canceled: Boolean = true) {
            if (!hasSetup) return
            hasSetup = false
            mNowContentView = mCurrentContentLayout.getChildAt(0)
            removePreView(canceled)
            removeShadowView()
        }

        /**
         * Remove view from previous Activity and add into current Activity
         *
         * @return Is view added successfully
         */
        private fun addPreView() {
            if (mCurrentContentLayout.childCount == 0) {
                mPreviousContentView = null
                return
            }

            val previousActivity = ActivityLifecycleHelper.getPreviousActivity()
            if (previousActivity == null) {
                mPreviousContentView = null
                return
            }

            //Previous activity not support to be swipeBack...
            if (previousActivity is SlideBackManager && !(previousActivity as SlideBackManager).canBeSlideBack()) {
                mPreviousContentView = null
                return
            }

            val previousLayout = getContentView(previousActivity)
            if (previousLayout == null || previousLayout.childCount == 0) {
                mPreviousContentView = null
                return
            }

            val previousPageView = PreviousPageView(mActivity)
            previousPageView.cacheView(previousLayout.getChildAt(0))
            mPreviousContentView = previousPageView
            mCurrentContentLayout.addView(mPreviousContentView, 0)
        }

        /**
         * Remove the PreviousContentView at current Activity and put it into previous Activity.
         */
        private fun removePreView(canceled: Boolean) {
            if (mPreviousContentView == null) return

            if (canceled)
                mCurrentContentLayout.removeView(mPreviousContentView)

            mPreviousContentView = null
            mNowContentView = null
        }

        /**
         * add shadow view on the left of content view
         */
        private fun addShadowView() {
            if (mShadowView == null) {
                val layoutParams = FrameLayout.LayoutParams(
                        SHADOW_WIDTH, FrameLayout.LayoutParams.MATCH_PARENT)
                mShadowView = ShadowView(mActivity).also {
                    it.x = (-SHADOW_WIDTH / 2).toFloat()
                }
                mCurrentContentLayout.addView(mShadowView, layoutParams)
            }
        }

        private fun removeShadowView() {
            if (mShadowView == null) return
            mCurrentContentLayout.removeView(mShadowView)
            mShadowView = null
        }
    }

    internal class ShadowView(context: Context) : View(context) {
        private val mDrawable: Drawable by lazy {
            val colors = intArrayOf(0x00000000, 0x0800000, 0x10000000)//分别为开始颜色，中间夜色，结束颜色
            return@lazy GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            mDrawable.setBounds(0, 0, measuredWidth, measuredHeight)
            mDrawable.draw(canvas)
        }
    }
}


interface SlideBackManager {
    fun getSlideActivity(): Activity
    /**
     * 是否支持滑动返回
     */
    fun supportSlideBack(): Boolean

    /**
     * 能否滑动返回至当前Activity
     */
    fun canBeSlideBack(): Boolean
}