package com.zzwl.bakeMedicine.weidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.g.base.extend.dp
import com.g.base.help.d
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.other.extend.getTextWidth


class TimeView : View {

    //刻度画笔
    private val dialPaint: Paint = Paint()
    //大圆弧画笔
    private val arcPaint: Paint = Paint()
    //小圆弧画笔
    private val arcPaint1: Paint = Paint()
    // 控件宽
    private var timeViewWidth: Int = 0
    // 控件高
    private var timeViewHeight: Int = 0
    // 刻度盘半径
    private var dialRadius: Int = 0
    // 大圆弧半径
    private var arcRadius: Int = 0
    // 小圆弧半径
    private var arcRadius1: Int = 0
    // 低刻度的高度
    private val scaleHeight = 3.dp()
    // 高刻度的高度
    private val scaleHeight1 = 6.dp()
    // 圆点画笔
    private val pointPaint: Paint = Paint()
    // 圆点半径
    private val pointRadius = 2.dp().toFloat()
    //文字画笔
    private val textPaint = Paint()
    //时间单位画笔
    private val textPaint1 = Paint()


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        dialPaint.isAntiAlias = true
        dialPaint.strokeWidth = 1.dp().toFloat()
        dialPaint.style = Paint.Style.STROKE

        arcPaint.isAntiAlias = true
        arcPaint.color = ContextCompat.getColor(context, R.color.colorGreen)
        arcPaint.strokeWidth = 1.dp().toFloat()
        arcPaint.style = Paint.Style.STROKE

        arcPaint1.isAntiAlias = true
        arcPaint1.color = ContextCompat.getColor(context, R.color.colorAccent)
        arcPaint1.strokeWidth = 2.dp().toFloat()
        arcPaint1.style = Paint.Style.STROKE

        pointPaint.isAntiAlias = true
        pointPaint.color = ContextCompat.getColor(context, R.color.colorGreen)
        pointPaint.strokeWidth = 2.dp().toFloat()
        pointPaint.style = Paint.Style.FILL

        textPaint.isAntiAlias = true
        textPaint.textSize = 14.dp().toFloat()
        textPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        textPaint.style = Paint.Style.STROKE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        textPaint1.isAntiAlias = true
        textPaint1.textSize = 14.dp().toFloat()
        textPaint1.color = ContextCompat.getColor(context, R.color.colorTextWeek)
        textPaint1.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawScale(canvas)
            drawArc(canvas)
            drawPoint(canvas)
            drawText(canvas)
        }
    }

    private fun drawText(canvas: Canvas) {
        d("aaaaaaaaaaaa1"+System.currentTimeMillis())
        canvas.save()
        d("aaaaaaaaaaaa2"+System.currentTimeMillis())
        canvas.translate(width / 2f, width / 2f)
        val tempWidth = textPaint.getTextWidth("135")
        val tempHeight = (textPaint.ascent() + textPaint.descent())
        canvas.drawText("135", -(tempWidth / 2f), tempHeight / 2, textPaint)
        val tempWidth1 = textPaint1.getTextWidth("h")
        canvas.drawText("h", -(tempWidth1 / 2f), 10.dp().toFloat(), textPaint1)
        d("aaaaaaaaaaaa3"+System.currentTimeMillis())
        canvas.restore()
        d("aaaaaaaaaaaa4"+System.currentTimeMillis())
    }

    private fun drawPoint(canvas: Canvas) {
        canvas.save()
        canvas.translate(width / 2f, width / 2f)
        canvas.drawCircle(arcRadius / 4f, -(arcRadius / 4f * Math.sqrt(15.00)).toFloat(), pointRadius, pointPaint)
        canvas.restore()
    }


    //绘制刻度
    private fun drawScale(canvas: Canvas) {
        canvas.save()
        canvas.translate(width / 2f, width / 2f)
        canvas.rotate(-90f)
        dialPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        for (i in 0..30) {
            if (i != 0 && i != 30) {
                if (i % 5 != 0)
                    canvas.drawLine(0f, -dialRadius.toFloat(), 0f, (-dialRadius + scaleHeight).toFloat(), dialPaint)
                else
                    canvas.drawLine(0f, -dialRadius.toFloat(), 0f, (-dialRadius + scaleHeight1).toFloat(), dialPaint)
            }
            if (i != 30)
                canvas.rotate(6f)
        }
        canvas.restore()
    }

    //绘制圆弧
    private fun drawArc(canvas: Canvas) {
        d("2222asasd")
        canvas.save()
        canvas.translate(width / 2f, width / 2f)
        val rectF = RectF(-arcRadius.toFloat(), -arcRadius.toFloat(), arcRadius.toFloat(), arcRadius.toFloat())
        val rectF1 = RectF(-arcRadius1.toFloat(), -arcRadius1.toFloat(), arcRadius1.toFloat(), arcRadius1.toFloat())
        canvas.drawArc(rectF, 180f, 180f, false, arcPaint)
        canvas.drawArc(rectF1, 180f, 180f, false, arcPaint1)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSize = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(defaultSize, (defaultSize / 5 * 3) + 8.dp())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 控件宽、高
        timeViewWidth = w
        timeViewHeight = h
        // 大圆弧半径
        arcRadius = timeViewWidth / 2 - 10.dp()
        // 小圆弧半径
        arcRadius1 = arcRadius - 7.dp()
        // 刻度盘半径
        dialRadius = arcRadius1 - 4.dp()
    }
}