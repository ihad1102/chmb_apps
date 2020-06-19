package com.g.base.help.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.annotation.FloatRange
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class GlideBlurTransformation @JvmOverloads constructor(
        private val context: Context,
        @FloatRange(from = 0.0,to = 25.00)  private val radius: Float = 25f,
        private val sampling: Int = 1
) : BitmapTransformation(context) {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = width / sampling
        val scaledHeight = height / sampling

        var bitmap: Bitmap? = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(context)
            rs!!.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT)
            output = Allocation.createTyped(rs, input!!.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            blur!!.setInput(input)
            blur.setRadius(radius)
            blur.forEach(output)
            output!!.copyTo(bitmap)
        } finally {
            if (rs != null) {
                rs.destroy()
            }
            if (input != null) {
                input.destroy()
            }
            if (output != null) {
                output.destroy()
            }
            if (blur != null) {
                blur.destroy()
            }
        }

        return bitmap
    }

    override fun getId(): String = javaClass.name
}