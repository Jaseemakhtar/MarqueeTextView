package com.jsync.qmarqueetextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView @JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attributeSet, defStyleAttr) {

    private var textX: Float
    private var textY: Float = 0f
    private var roller: ValueAnimator? = null
    private val textBounds: Rect by lazy {
        Rect()
    }
    private var textHeight: Int
    private var textWidth: Int
    private var marqueeText: String
    private var marqueeDuration: Long
    private var marqueeDelay: Long
    private var isAnimSet: Boolean = false
    private var isLayoutReady: Boolean = false
    private var clipWidth: Int = 0

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MarqueeTextView)
        textX = paddingLeft.toFloat()
        textHeight = getTextHeight(text.toString())
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
        roller?.removeAllUpdateListeners()
        roller?.cancel()
        textX = paddingLeft.toFloat()
        invalidate()
    }

    fun start() {
        isAnimSet = true
        if (isAnimSet && isLayoutReady) {
            if (textWidth > width - (paddingStart + paddingEnd)) {
                // need to roll
                marqueeText = "$text\t\t\t$text"
                roller = ValueAnimator.ofFloat(
                    paddingLeft.toFloat(),
                    -1f * (getTextWidth("\t\t\t") + textWidth - paddingLeft)
                )
                roller?.startDelay = marqueeDelay
                roller?.duration = marqueeDuration
                roller?.interpolator = AccelerateDecelerateInterpolator()
                roller?.addUpdateListener {
                    textX = it.animatedValue as Float
                    invalidate()
                }
                roller?.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        roller?.start()
                    }
                })
                roller?.start()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            textY = ((height / 2 + textHeight / 2).toFloat())
            clipWidth = width - paddingRight
            isLayoutReady = true
            invalidate()
            if (isAnimSet) {
                start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        roller?.removeAllUpdateListeners()
        roller?.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas?.clipRect(paddingLeft, 0, clipWidth, height)
        } else {
            canvas?.clipRect(
                paddingLeft.toFloat(),
                0f,
                clipWidth.toFloat(),
                height.toFloat(),
                Region.Op.INTERSECT
            )
        }
        canvas?.drawText(marqueeText, textX, textY, paint)
    }

    private fun getTextWidth(txt: String? = "Dummy"): Int {
        return paint.measureText(txt).toInt()
    }

    private fun getTextHeight(txt: String? = "Dummy"): Int {
        paint.getTextBounds(txt, 0, txt!!.length, textBounds)
        return textBounds.height()
    }
}
