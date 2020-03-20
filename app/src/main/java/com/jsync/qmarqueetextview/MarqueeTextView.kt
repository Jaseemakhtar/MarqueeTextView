package com.jsync.qmarqueetextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attributeSet, defStyleAttr) {

    private var textX: Float
    private var textY: Float
    private lateinit var roller: ValueAnimator
    private val textBounds = Rect()
    private var textHeight: Int
    private var textWidth: Int
    private var marqueeText: String
    private var marqueeDuration: Long
    private var marqueeDelay: Long

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MarqueeTextView)
        isSingleLine = true
        textX = paddingLeft.toFloat()
        textY = 0f
        textHeight = getTextHeight(text as String?)
        textWidth = getTextWidth(text as String)
        marqueeText = text.toString()
        marqueeDuration =
            typedArray.getInteger(R.styleable.MarqueeTextView_marqueeDuration, 3000).toLong()
        marqueeDelay =
            typedArray.getInteger(R.styleable.MarqueeTextView_marqueeDelay, 1000).toLong()
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            textY = ((height / 2 + textHeight / 2).toFloat())
            if(textWidth > width - (paddingLeft + paddingRight)){
                // need to roll
                marqueeText = "$text\t\t\t$text"
                roller = ValueAnimator.ofFloat(paddingLeft.toFloat(), -1f * (getTextWidth("\t\t\t") + textWidth - paddingLeft))
                roller.startDelay = marqueeDelay
                roller.duration = marqueeDuration
                roller.addUpdateListener {
                    textX = it.animatedValue as Float
                    invalidate()
                }
                roller.start()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawText(marqueeText, textX, textY, paint)
    }

    private fun getTextWidth(txt: String? = "Dummy"): Int{
        return paint.measureText(txt).toInt()
    }

    private fun getTextHeight(txt: String? = "Dummy"): Int{
        paint.getTextBounds(txt, 0, txt!!.length, textBounds)
        return textBounds.height()
    }
}