package com.g.base.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat

object TinterUtil{
    fun tintDrawable(drawable: Drawable, colors: ColorStateList): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTintList(wrappedDrawable, colors)
        return wrappedDrawable
    }
}