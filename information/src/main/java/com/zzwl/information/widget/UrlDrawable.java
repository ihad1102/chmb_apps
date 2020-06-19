package com.zzwl.information.widget;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by G on 2017/9/5 0005.
 */
public class UrlDrawable extends BitmapDrawable {
    private Drawable drawable;

    @SuppressWarnings("deprecation")
    public UrlDrawable() {
    }
    @Override
    public void draw(Canvas canvas) {
        if (drawable != null){
            drawable.draw(canvas);
        }
    }
    public Drawable getDrawable() {
        return drawable;
    }
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}