package com.zzwl.bakeMedicine.weidget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.g.base.extend.dp
import com.g.base.help.d
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.other.extend.getTextWidth

class ProgressRingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    @SuppressLint("CustomViewStyleable")
    private val ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressRing)
    private val progressStartColor = ta.getColor(R.styleable.ProgressRing_pr_progress_start_color, Color.YELLOW)
    private val progressEndColor = ta.getColor(R.styleable.ProgressRing_pr_progress_end_color, progressStartColor)
    private val bgStartColor = ta.getColor(R.styleable.ProgressRing_pr_bg_start_color, Color.LTGRAY)
    private val bgMidColor = ta.getColor(R.styleable.ProgressRing_pr_bg_mid_color, bgStartColor)
    private val bgEndColor = ta.getColor(R.styleable.ProgressRing_pr_bg_end_color, bgStartColor)
    private val progress = ta.getInt(R.styleable.ProgressRing_pr_progress, 0)
    private val progressWidth = ta.getDimension(R.styleable.ProgressRing_pr_progress_width, 6.dp().toFloat())
    private val startAngle = ta.getInt(R.styleable.ProgressRing_pr_start_angle, -90)
    private val sweepAngle = ta.getInt(R.styleable.ProgressRing_pr_sweep_angle, 360)
    private val showAnim = ta.getBoolean(R.styleable.ProgressRing_pr_show_anim, true)
    private val prText = ta.getString(R.styleable.ProgressRing_pr_text)
    private var pRectF: RectF? = null

    private var unitAngle: Float = 0f

    private var curProgress = 0
    //两个画笔
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    //文本画笔
    private val textPaint = Paint()
    private val text1Paint = Paint()

    init {
        ta.recycle()
        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeCap = Paint.Cap.ROUND
        bgPaint.strokeWidth = progressWidth
        unitAngle = sweepAngle / 100f

        progressPaint.style = Paint.Style.STROKE;
        progressPaint.strokeCap = Paint.Cap.ROUND;
        progressPaint.strokeWidth = progressWidth;

        textPaint.isAntiAlias = true
        textPaint.textSize = 14.dp().toFloat()
        textPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        textPaint.style = Paint.Style.STROKE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private var mMeasureHeight: Int = 0

    private var mMeasureWidth: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(defaultSize, defaultSize)
        mMeasureWidth = defaultSize
        mMeasureHeight = defaultSize
    }

    override fun onDraw(canvas: Canvas?) {
        if (pRectF == null) {
            val halfProgressWidth = progressWidth / 2
            pRectF = RectF(halfProgressWidth + paddingLeft,
                    halfProgressWidth + paddingTop,
                    mMeasureWidth - halfProgressWidth - paddingRight,
                    mMeasureHeight - halfProgressWidth - paddingBottom)
        }
        if (!showAnim) {
            curProgress = progress
        }
        if (canvas != null) {
            drawBg(canvas)
            drawProgress(canvas)
            drawText(canvas)
        }
        if (curProgress < progress) {
            curProgress++
            postInvalidate()
        }
    }

    private fun drawText(canvas: Canvas) {
        val textWidth = textPaint.getTextWidth(prText)
        val textHeight = (textPaint.ascent() + textPaint.descent())
        canvas.drawText(prText, mMeasureWidth / 2f - textWidth / 2f, mMeasureHeight / 2f - textHeight/2f, textPaint)
    }

    private fun drawProgress(canvas: Canvas) {
        var i = 0
        val end = (curProgress * unitAngle).toInt()
        while (i <= end) {
            progressPaint.color = getGradient(i / end.toFloat(), progressStartColor, progressEndColor)
            canvas.drawArc(pRectF,
                    (startAngle + i).toFloat(),
                    8f,
                    false,
                    progressPaint)
            i+=8
        }
    }

    // 只需要画进度之外的背景即可
    private fun drawBg(canvas: Canvas) {
        bgPaint.color = bgEndColor
        canvas.drawArc(pRectF,
                0f,
                sweepAngle.toFloat(),
                false,
                bgPaint)

    }

    /**
     * 输入进度当前位置,开始的颜色,结束的颜色,根据进度的百分比计算并返回当前进度的颜色
     */
    private fun getGradient(fraction: Float, startColor: Int, endColor: Int): Int {
        var tempFraction = fraction
        if (tempFraction > 1) tempFraction = 1f
        val alphaStart = Color.alpha(startColor)
        val redStart = Color.red(startColor)
        val blueStart = Color.blue(startColor)
        val greenStart = Color.green(startColor)
        val alphaEnd = Color.alpha(endColor)
        val redEnd = Color.red(endColor)
        val blueEnd = Color.blue(endColor)
        val greenEnd = Color.green(endColor)
        val alphaDifference = alphaEnd - alphaStart
        val redDifference = redEnd - redStart
        val blueDifference = blueEnd - blueStart
        val greenDifference = greenEnd - greenStart
        val alphaCurrent = (alphaStart + tempFraction * alphaDifference).toInt()
        val redCurrent = (redStart + tempFraction * redDifference).toInt()
        val blueCurrent = (blueStart + tempFraction * blueDifference).toInt()
        val greenCurrent = (greenStart + tempFraction * greenDifference).toInt()
        return Color.rgb(redCurrent, greenCurrent, blueCurrent)
    }
}