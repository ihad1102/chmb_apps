package com.g.base.dataBinding

import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.g.base.R
import com.g.base.extend.setSpace
import com.g.base.help.glide.GlideCircleTransform
import com.g.base.help.glide.GlideRoundTransform
import com.g.base.help.tryCatch


/**
 * Created by G on 2017/8/12 0012.
 */
//设置视图宽高
@BindingAdapter("android:layout_height", "android:layout_width", requireAll = false)
fun setLayoutSpace(view: View, height: Number? = null, width: Number? = null) {
    view.setSpace(height, width)
}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("app:background")
fun setViewBackground(view: View, resource: Int) {
    view.setBackgroundResource(resource)
}

@BindingAdapter("app:backgroundTint")
fun setBackgroundTint(view: View, color: Int) {
    tryCatch { view.background?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN) }
}

@BindingAdapter("app:drawableTint")
fun setDrawableTint(view: TextView, color: Int) {
    tryCatch {
        view.compoundDrawables?.forEach {
            it?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

@BindingAdapter("app:imageTint")
fun setTint(view: ImageView, resource: Int) {
    tryCatch { view.setColorFilter(resource, PorterDuff.Mode.SRC_IN) }
}

//设置背景图片 圆角 OR 圆形图片 OR 默认图片
@BindingAdapter("app:image", "app:imageRadius", "app:imageCircle", "app:placeholder", "app:error", requireAll = false)
fun setBackgroundImage(view: ImageView, oldSrc: Any? = null, oldRadius: Number? = null, oldCircle: Boolean? = null, oldPlaceholder: Int? = null, oldError: Int? = null,
                       src: Any? = null, radius: Number? = null, circle: Boolean? = null, placeholder: Int? = null, error: Int? = null) {
    if (oldSrc == src) return
    Glide.with(view.context).load(src)
            .asBitmap()
            .placeholder(placeholder ?: R.drawable.load_placeholder)
            .error(error ?: R.drawable.load_error)
            .apply {
                if (circle != null && circle) {
                    transform(GlideCircleTransform(view.context))
                } else if (radius != null && radius.toInt() > 0) {
                    transform(GlideRoundTransform(view.context, radius.toInt()))
                } else {
                    dontTransform()
                }
                crossFade(300)
                into(view)
            }
}

@BindingAdapter("app:selectedId", "app:houseId", "app:type", requireAll = true)
fun setSrc(imageView: ImageView, observableField: ObservableField<Int?>?, houseId: Int, type: Int) {
    if (observableField?.get() == houseId)
        when (type) {
            0 -> imageView.setImageResource(R.drawable.ic_stop_tobacco_selected)
            1 -> imageView.setImageResource(R.drawable.ic_running_tobacco_selected)
            2 -> imageView.setImageResource(R.drawable.ic_warning_tobacco_selected)
        }
    else when (type) {
        0 -> imageView.setImageResource(R.drawable.ic_stop_tobacco)
        1 -> imageView.setImageResource(R.drawable.ic_running_tobacco)
        2 -> imageView.setImageResource(R.drawable.ic_warning_tobacco)
    }
}


