package com.jianjun.heartanimate

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.FloatRange


class HeartView : View {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val heartBmp = BitmapFactory.decodeResource(resources, R.mipmap.ic_heart)
    private val linePath = Path()
    private val rectF = RectF()
    private val lineMatrix = Matrix()
    private var pathLength = 0f
    private val offset = dp2px(10f)

    @FloatRange(from = 0.0, to = 1.0)
    var lineProgress = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        linePaint.color = Color.WHITE
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dp2px(2f)
        linePaint.strokeJoin = Paint.Join.ROUND
    }

    private fun setPath() {
        linePath.reset()
        linePath.moveTo(0f, 0f)
        linePath.lineTo(10f, 0f)
        linePath.lineTo(13f, -5f)
        linePath.lineTo(16f, 0f)
        linePath.lineTo(19f, -10f)
        linePath.lineTo(24f, 6f)
        linePath.lineTo(27f, -3f)
        linePath.lineTo(29f, 0f)
        linePath.lineTo(41f, 0f)
        val rate = width / 41f
        lineMatrix.setScale(rate, rate)
        lineMatrix.postTranslate(0f, height / 2.2f)
        linePath.transform(lineMatrix)
        pathLength = PathMeasure(linePath, false).length
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthModel = MeasureSpec.getMode(widthMeasureSpec)
        var heartWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (widthModel != MeasureSpec.EXACTLY) {
            heartWidth = heartBmp.width
        }
        val heightModel = MeasureSpec.getMode(heightMeasureSpec)
        var heartHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (heightModel != MeasureSpec.EXACTLY) {
            heartHeight = heartBmp.height
        }
        setMeasuredDimension(heartWidth, heartHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            setPath()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val pathEffect: PathEffect = DashPathEffect(
            floatArrayOf(pathLength, pathLength),
            pathLength - pathLength * lineProgress
        )
        linePaint.pathEffect = pathEffect
        canvas?.drawBitmap(heartBmp, null, rectF, null)
        canvas?.drawPath(linePath, linePaint)
    }

    fun startAnimate() {
        post {
            val heartAnimator = ValueAnimator.ofFloat(0f, 1f)
            heartAnimator.duration = 1000
            heartAnimator.interpolator = OvershootInterpolator()
            heartAnimator.addUpdateListener {
                rectF.top = 0f
                val ratio = it.animatedValue as Float
                val w = (width - offset) * ratio
                val h = (height - offset) * ratio
                rectF.left = (width - w) / 2
                rectF.top = 0f
                rectF.right = rectF.left + w
                rectF.bottom = h
                invalidate()
            }
            val lineAnimator = ValueAnimator.ofFloat(0f, 1f)
            lineAnimator.duration = 500
            lineAnimator.interpolator = LinearInterpolator()
            lineAnimator.startDelay = 500
            lineAnimator.addUpdateListener {
                lineProgress = it.animatedValue as Float
                invalidate()
            }
            heartAnimator.start()
            lineAnimator.start()
        }
    }

    private fun dp2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

}