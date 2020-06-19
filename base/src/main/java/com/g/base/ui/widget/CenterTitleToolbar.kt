package com.g.base.ui.widget

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.Nullable
import android.support.annotation.StringRes
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import com.g.base.R

class CenterTitleToolbar @JvmOverloads constructor(context: Context, @Nullable attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.toolbarStyle) : Toolbar(context, attrs, defStyleAttr) {
    private var titleTextView: AppCompatTextView? = null
    private var titleTextAppearance: Int = 0
    private var titleTextColor: Int = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CenterTitleToolbar, defStyleAttr, 0)
        titleTextAppearance = a.getResourceId(R.styleable.CenterTitleToolbar_titleTextAppearance, 0)
        a.recycle()
    }

    override fun setTitle(title: CharSequence) {
        if (!TextUtils.isEmpty(title)) {
            if (titleTextView == null) {
                val context = context
                titleTextView = AppCompatTextView(context)
                titleTextView!!.setSingleLine()
                titleTextView!!.ellipsize = TextUtils.TruncateAt.END
                val generateDefaultLayoutParams = generateDefaultLayoutParams()
                generateDefaultLayoutParams.gravity = Gravity.CENTER
                titleTextView!!.layoutParams = generateDefaultLayoutParams
                if (titleTextAppearance != 0) {
                    titleTextView!!.setTextAppearance(context, titleTextAppearance)
                }
                if (titleTextColor != 0) {
                    titleTextView!!.setTextColor(titleTextColor)
                }
                addView(titleTextView)
            }
        } else if (titleTextView != null && titleTextView!!.parent == this) {
            removeView(titleTextView)
            titleTextView = null
        }
        if (titleTextView != null) {
            titleTextView!!.text = title
        }
    }

    override fun setTitle(@StringRes resId: Int) {
        title = resources.getString(resId)
    }

    override fun setTitleTextColor(@ColorInt color: Int) {
        titleTextColor = color
        titleTextView?.setTextColor(color)
    }
}