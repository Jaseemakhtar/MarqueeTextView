package com.jsync.qmarqueetextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.min


class MarqueeTextView
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private var textX: Float
    private var text: String?
    private var marqueeText: String
    private var textSize: Int
    private var textHeight: Int
    private var textWidth: Int
    private var textColor: Int
    private lateinit var roller: ValueAnimator

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MarqueeTextView)
        text = typedArray.getString(R.styleable.MarqueeTextView_marqueeText)
        marqueeText = text.toString()
        textSize = typedArray.getDimensionPixelSize(R.styleable.MarqueeTextView_marqueeTextSize, 36)
        textColor = typedArray.getColor(R.styleable.MarqueeTextView_marqueeTextColor, Color.DKGRAY)
        textHeight = getTextHeight(text)
        textWidth = getTextWidth(text)
        setPadding(10, 10, 10, 10)
        textX = paddingLeft.toFloat()
        typedArray.recycle()
    }

    fun setText(txt: String){
        text = txt
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        drawText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val desiredHeight = paddingTop + paddingBottom + textHeight

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val desiredWidth = paddingLeft + paddingRight + textWidth

        var hh = 0
        var ww = 0

        hh = when (heightMode) {
            MeasureSpec.EXACTLY -> { // Must be this size - when set by constant values and also when values are given by parent i,e. set as MATCH_PARENT
                heightSize
            }
            MeasureSpec.AT_MOST -> { // Cannot be bigger than this size - when WRAP_CONTENT
                min(desiredHeight, heightSize)
            }
            else -> { //Be whatever you want
                desiredHeight
            }
        }

        ww = when(widthMode){
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredWidth, widthSize)
            }
            else -> {
                desiredWidth
            }
        }
        Log.d("jtest", MeasureSpec.toString(heightMeasureSpec))
        setMeasuredDimension(ww, hh)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            if(textWidth > width){
                // need to animate
                marqueeText = "$text\t\t\t$text"
                roller = ValueAnimator.ofFloat(paddingLeft.toFloat(), -1f * (getTextWidth("\t\t\t") + textWidth - paddingLeft))
                roller.startDelay = 1000
                roller.duration = 3000
                roller.addUpdateListener {
                    textX = it.animatedValue as Float
                    invalidate()
                }
                roller.start()
            }
        }
    }

    private fun drawText(canvas: Canvas?){
        val paint = Paint()
        paint.color = textColor
        paint.textSize = textSize.toFloat()
        textWidth = getTextWidth(text)
        textHeight = getTextHeight(text)
        canvas?.drawText(marqueeText, textX, (height / 2 + textHeight / 2).toFloat(), paint)
    }

    private fun getTextWidth(txt: String? = "Dummy"): Int{
        val paint = Paint()
        paint.textSize = textSize.toFloat()
        return paint.measureText(txt).toInt()
    }

    private fun getTextHeight(txt: String? = "Dummy"): Int{
        val paint = Paint()
        val textBounds = Rect()
        paint.textSize = textSize.toFloat()
        paint.getTextBounds(txt, 0, txt!!.length, textBounds)
        return textBounds.height()
    }

}
