package com.mini.amimatch

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.mini.amimatch.SquareImageView


/**
 * DatingApp
 * https://github.com/quintuslabs/DatingApp
 * Created on 25-sept-2018.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 * modified by AdityaUpreti
 */
class PulsatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private val mViews: MutableList<View> = ArrayList()
    private var mCount: Int
    private var mDuration: Int
    private var mRepeat: Int
    private var mStartFromScratch: Boolean
    private var mColor: Int
    private var mInterpolator: Int
    private var mAnimatorSet: AnimatorSet? = null
    private val mPaint: Paint?
    var mRadius = 0f
    var mCenterX = 0f
    var mCenterY = 0f
    private var mIsStarted = false
    private val mAnimatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {
            mIsStarted = true
        }

        override fun onAnimationEnd(animator: Animator) {
            mIsStarted = false
        }

        override fun onAnimationCancel(animator: Animator) {
            mIsStarted = false
        }

        override fun onAnimationRepeat(animator: Animator) {}
    }

    init {
        val attr = context.theme.obtainStyledAttributes(
            attrs, R.styleable.Pulsator4Droid, 0, 0
        )
        mCount = DEFAULT_COUNT
        mDuration = DEFAULT_DURATION
        mRepeat = DEFAULT_REPEAT
        mStartFromScratch = DEFAULT_START_FROM_SCRATCH
        mColor = DEFAULT_COLOR
        mInterpolator = DEFAULT_INTERPOLATOR
        try {
            mCount = attr.getInteger(R.styleable.Pulsator4Droid_pulse_count, DEFAULT_COUNT)
            mDuration = attr.getInteger(
                R.styleable.Pulsator4Droid_pulse_duration,
                DEFAULT_DURATION
            )
            mRepeat = attr.getInteger(R.styleable.Pulsator4Droid_pulse_repeat, DEFAULT_REPEAT)
            mStartFromScratch = attr.getBoolean(
                R.styleable.Pulsator4Droid_pulse_startFromScratch,
                DEFAULT_START_FROM_SCRATCH
            )
            mColor = attr.getColor(R.styleable.Pulsator4Droid_pulse_color, DEFAULT_COLOR)
            mInterpolator = attr.getInteger(
                R.styleable.Pulsator4Droid_pulse_interpolator,
                DEFAULT_INTERPOLATOR
            )
        } finally {
            attr.recycle()
        }

        mPaint = Paint()
        mPaint.setAntiAlias(true)
        mPaint.setStyle(Paint.Style.FILL)
        mPaint.setColor(mColor)

        build()
    }

    @Synchronized
    fun start() {
        if (mAnimatorSet == null || mIsStarted) {
            return
        }
        mAnimatorSet!!.start()
        if (!mStartFromScratch) {
            val animators = mAnimatorSet!!.childAnimations
            for (animator in animators) {
                val objectAnimator = animator as ObjectAnimator
                val delay = objectAnimator.startDelay
                objectAnimator.startDelay = 0
                objectAnimator.currentPlayTime = mDuration - delay
            }
        }
    }

    @Synchronized
    fun stop() {
        if (mAnimatorSet == null || !mIsStarted) {
            return
        }
        mAnimatorSet!!.end()
    }

    @get:Synchronized
    val isStarted: Boolean
        get() = (mAnimatorSet != null && mIsStarted)

    var count: Int
        get() = mCount
        set(count) {
            require(!(count < 0)) { "Count cannot be negative" }
            if (count != mCount) {
                mCount = count
                reset()
                invalidate()
            }
        }

    var duration: Int
        get() = mDuration
        set(millis) {
            if (millis < 0) {
                throw IllegalArgumentException("Duration cannot be negative")
            }
            if (millis != mDuration) {
                mDuration = millis
                reset()
                invalidate()
            }
        }

    var color: Int
        get() = mColor
        set(color) {
            if (color != mColor) {
                mColor = color
                if (mPaint != null) {
                    mPaint.color = color
                }
            }
        }

    var interpolator: Int
        get() = mInterpolator
        set(type) {
            if (type != mInterpolator) {
                mInterpolator = type
                reset()
                invalidate()
            }
        }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        mCenterX = width * 0.5f
        mCenterY = height * 0.5f
        mRadius = Math.min(width, height) * 0.5f
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun clear() {
        stop()
        for (view in mViews) {
            removeView(view)
        }
        mViews.clear()
    }

    private fun build() {
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        val repeatCount = if (mRepeat == INFINITE) ObjectAnimator.INFINITE else mRepeat
        val animators: MutableList<Animator> = ArrayList()
        for (index in 0 until mCount) {
            val pulseView = PulseView(context)
            pulseView.setScaleX(0f)
            pulseView.setScaleY(0f)
            pulseView.setAlpha(1f)
            addView(pulseView, index, layoutParams)
            mViews.add(pulseView)
            val delay = (index * mDuration / mCount).toLong()
            val scaleXAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleX", 0f, 1f)
            scaleXAnimator.repeatCount = repeatCount
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = delay
            animators.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(pulseView, "ScaleY", 0f, 1f)
            scaleYAnimator.repeatCount = repeatCount
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = delay
            animators.add(scaleYAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(pulseView, "Alpha", 1f, 0f)
            alphaAnimator.repeatCount = repeatCount
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = delay
            animators.add(alphaAnimator)
        }
        mAnimatorSet = AnimatorSet()
        mAnimatorSet!!.playTogether(animators)
        mAnimatorSet!!.interpolator = createInterpolator(mInterpolator)
        mAnimatorSet!!.setDuration(mDuration.toLong())
        mAnimatorSet!!.addListener(mAnimatorListener)
    }

    private fun reset() {
        val isStarted = isStarted
        clear()
        build()
        if (isStarted) {
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
            mAnimatorSet = null
        }
    }

    private inner class PulseView(context: Context?) : View(context) {
        override fun onDraw(canvas: Canvas) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint!!)
        }
    }

    companion object {
        const val INFINITE = 0
        const val INTERP_LINEAR = 0
        const val INTERP_ACCELERATE = 1
        const val INTERP_DECELERATE = 2
        const val INTERP_ACCELERATE_DECELERATE = 3
        private const val DEFAULT_COUNT = 4
        private val DEFAULT_COLOR = Color.rgb(0, 132, 208)
        private const val DEFAULT_DURATION = 7000
        private val DEFAULT_REPEAT = INFINITE
        private const val DEFAULT_START_FROM_SCRATCH = true
        private val DEFAULT_INTERPOLATOR = INTERP_LINEAR

        private fun createInterpolator(type: Int): Interpolator {
            return when (type) {
                INTERP_ACCELERATE -> AccelerateInterpolator()
                INTERP_DECELERATE -> DecelerateInterpolator()
                INTERP_ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
                else -> LinearInterpolator()
            }
        }
    }
}
