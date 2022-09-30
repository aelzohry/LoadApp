package com.udacity.aelzohry.loadapp.ui.loading_button

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.aelzohry.loadapp.R
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

    var loadingState: LoadingState by Delegates.observable(LoadingState.Completed) { _, _, newValue ->
        if (newValue == LoadingState.Loading) {
            progress = 0f
            valueAnimator.start()
        } else {
            valueAnimator.cancel()
        }
        invalidate()
    }

    private val buttonText: String
        get() = if (loadingState == LoadingState.Loading) "We are loading" else "Download"

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 70.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            bgColor = getColor(R.styleable.LoadingButton_backgroundColor,
                ContextCompat.getColor(context, R.color.button_background))

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
        paint.color = bgColor
        canvas?.drawRect(0f, 0f, widthSize, heightSize, paint)

        // Loading
        if (loadingState == LoadingState.Loading) {
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