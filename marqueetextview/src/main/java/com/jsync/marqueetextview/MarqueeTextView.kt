package com.jsync.marqueetextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attributeSet, defStyleAttr) {

    private var textX: Float
    private var textY: Float = 0f
    private var rollerAnimator: ValueAnimator? = null

    private var textHeight: Float
    private var textWidth: Int
    private var marqueeText: String
    private var marqueeDuration: Long
    private var marqueeDelay: Long
    private var isAnimSet: Boolean = false
    private var isLayoutReady: Boolean = false

    private val path = Path()

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MarqueeTextView)
        setSingleLine(true)
        textX = paddingLeft.toFloat()
        textHeight = getTextHeight()
        textWidth = getTextWidth(text.toString())
        marqueeText = text.toString()
        paint.color = currentTextColor
        marqueeDuration =
            typedArray.getInteger(R.styleable.MarqueeTextView_marqueeDuration, 3000).toLong()
        marqueeDelay =
            typedArray.getInteger(R.styleable.MarqueeTextView_marqueeDelay, 1000).toLong()
        typedArray.recycle()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        marqueeText = text.toString()
        textWidth = getTextWidth(marqueeText)
        cancelRollerAnimation()
        textX = paddingLeft.toFloat()
        invalidate()
    }

    fun start() {
        isAnimSet = true
        if (isAnimSet && isLayoutReady) {
            if (textWidth > width - (paddingStart + paddingEnd)) {
                // need to roll
                marqueeText = "$text\t\t\t$text"
                rollerAnimator = ValueAnimator.ofFloat(
                    paddingLeft.toFloat(),
                    -1.0f * (getTextWidth("\t\t\t") + textWidth - paddingLeft)
                )
                rollerAnimator?.startDelay = marqueeDelay
                rollerAnimator?.duration = marqueeDuration
                rollerAnimator?.interpolator = AccelerateDecelerateInterpolator()
                rollerAnimator?.addUpdateListener {
                    textX = it.animatedValue as Float
                    invalidate()
                }
                rollerAnimator?.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        rollerAnimator?.start()
                    }
                })
                rollerAnimator?.start()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!path.isEmpty) {
            path.reset()
        }
        path.addRect(
            paddingLeft.toFloat(),
            0.0f,
            (w - paddingRight).toFloat(),
            h.toFloat(),
            Path.Direction.CW
        )
        textHeight = getTextHeight()
        textY =
            ((((height - (paddingTop + paddingBottom)) - textHeight) / 2) + textHeight) - (paint.fontMetrics.descent)
        textY += paddingTop
        isLayoutReady = true
        invalidate()
        if (isAnimSet) {
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelRollerAnimation()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.save()
            it.clipPath(path)
            it.drawText(marqueeText, textX, textY, paint)
            it.restore()
        }
    }

    private fun getTextWidth(text: String): Int {
        return paint.measureText(text).toInt()
    }

    private fun getTextHeight(): Float {
        val fontMetrics = paint.fontMetrics
        return fontMetrics.descent + -fontMetrics.ascent
    }

    private fun cancelRollerAnimation() {
        if (rollerAnimator?.isRunning == true) {
            rollerAnimator?.cancel()
        }
        rollerAnimator?.removeAllUpdateListeners()
        rollerAnimator?.removeAllListeners()
    }
}
