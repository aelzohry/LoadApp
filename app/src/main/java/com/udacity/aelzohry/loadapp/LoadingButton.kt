package com.udacity.aelzohry.loadapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    @ColorInt
    private var bgColor = 0

    @ColorInt
    private var textColor = 0

    @ColorInt
    private var progressColor = 0

    @ColorInt
    private var progressBackgroundColor = 0

    private var progress = 0f

    private val valueAnimator = ValueAnimator
        .ofFloat(0f, 100f)
        .setDuration(5_000)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, _ ->
        invalidate()
    }

    private val buttonText: String
        get() = if (buttonState == ButtonState.Loading) "Downloading" else "Download"

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 70.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun performClick(): Boolean {
        buttonState = when (buttonState) {
            ButtonState.Completed -> ButtonState.Loading
            ButtonState.Loading -> ButtonState.Completed
        }

        if (buttonState == ButtonState.Loading) {
            progress = 0f
            valueAnimator.start()
        }
        return super.performClick()
    }

    init {
        isClickable = true

        bgColor = (background as? ColorDrawable)?.color
            ?: ContextCompat.getColor(context, R.color.button_background)

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.button_text))

            progressColor = getColor(R.styleable.LoadingButton_progressColor,
                ContextCompat.getColor(context, R.color.button_progress))

            progressBackgroundColor = getColor(R.styleable.LoadingButton_progressBackgroundColor,
                ContextCompat.getColor(context, R.color.button_progress_background))
        }

        setupAnimator()
    }

    private fun setupAnimator() {
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress = 0f
                animation.start()
            }
        })
    }

    private var circularProgressRect = RectF(
        740f,
        40f,
        820f,
        120f
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Background
        setBackgroundColor(bgColor)

        // Loading
        if (buttonState == ButtonState.Loading) {
            // Linear Progress
            paint.color = progressBackgroundColor
            canvas?.drawRect(0f, 0f, widthSize * progress / 100, heightSize, paint)

            // Circular Progress
            paint.color = progressColor
            canvas?.drawArc(circularProgressRect, 0f, 360 * (progress / 100), true, paint)
        }

        // Text
        paint.color = textColor
        canvas?.drawText(buttonText, widthSize / 2, (heightSize + 40) / 2, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

}